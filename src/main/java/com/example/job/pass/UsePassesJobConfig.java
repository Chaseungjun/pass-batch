package com.example.job.pass;

import com.example.booking.BookingStatus;
import com.example.booking.entity.Booking;
import com.example.booking.repository.BookingRepository;
import com.example.pass.entity.Pass;
import com.example.pass.repository.PassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Future;

@Configuration
@RequiredArgsConstructor
public class UsePassesJobConfig { // 수업 종료 후 이용권 차감

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final PassRepository passRepository;
    private final BookingRepository bookingRepository;

    private final int CHUCK_SIZE = 10;

    public Job usePassesJob(){
        return jobBuilderFactory.get("usePassesJob")
                .start(usePassesStep())
                .build();
    }

    public Step usePassesStep(){
        return stepBuilderFactory.get("usePassesStep")
                .<Booking, Future<Booking>>chunk(CHUCK_SIZE)
                .reader(usePassesItemReader())
                .processor(usePassesAsyncItemProcessor())
                .writer(usePassesAsyncItemWriter())
                .build();
    }

    public JpaCursorItemReader<Booking> usePassesItemReader(){
        return new JpaCursorItemReaderBuilder<Booking>()
                .name("usePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from Booking b join fetch b.pass where b.status = :status and b.usedPass = false and b.endedAt < :endedAt")
                // 예약 완료된 정보를 가져온다
                .parameterValues(Map.of("status", BookingStatus.COMPLETED , "endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<Booking, Booking> usePassesItemProcessor(){
        return booking -> {
            Pass pass = booking.getPass();
            pass.setRemainingCount(pass.getRemainingCount() - 1);
            booking.setPass(pass);
            booking.setUsedPass(true);  // 여기서 UsedPass를 true로 설정하면 이용가능횟수가 남아있을때 해당이용권으로 예약이 가능한가?
            return booking;
        };
    }
    @Bean
    public ItemWriter<Booking> usePassesItemWriter(){
        // processor에서 카운트 1 감소, UsedPass true로 데이터를 업데이트 했고, 여기서는 Db에 해당 값들을 저장
        return bookings -> {
            for(Booking booking : bookings){
                int updateCount = passRepository.updateRemainingCount(booking.getPassSeq(), booking.getPass().getRemainingCount());
                if (updateCount > 0){
                    bookingRepository.updateUsedPass(booking.getPassSeq(), booking.isUsedPass());
                }
            }
        };
    }

    @Bean
    public AsyncItemProcessor<Booking, Booking> usePassesAsyncItemProcessor(){
        // Async : 별도의 스레드를 할당( 새로운 스레드 위에서 작동하는 멀티 스레드 방식
        AsyncItemProcessor<Booking, Booking> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(usePassesItemProcessor());  // usePassesItemProcessor로 위임하고 결과를 Future에 저장합니다.
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return asyncItemProcessor;
    }

    @Bean
    public AsyncItemWriter<Booking> usePassesAsyncItemWriter(){
        AsyncItemWriter<Booking> bookingAsyncItemWriter = new AsyncItemWriter<>();
        bookingAsyncItemWriter.setDelegate(usePassesItemWriter());
        return bookingAsyncItemWriter;
    }


}
