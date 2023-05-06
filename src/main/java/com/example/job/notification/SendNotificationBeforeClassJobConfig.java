package com.example.job.notification;

import com.example.booking.BookingStatus;
import com.example.booking.entity.Booking;
import com.example.notification.NotificationEvent;
import com.example.notification.NotificationMapStruct;
import com.example.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class SendNotificationBeforeClassJobConfig {

    private final int CHUCK_SIZE = 10;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final SendNotificationItemWriter sendNotificationItemWriter;

    @Bean
    public Job sendNotificationBeforeClassJob(){  // step이 2개 필요하다 (알람 대상을 정하는 step, 알람을 발송하는 step) 순서도 주의
        return jobBuilderFactory.get("SendNotificationBeforeClassJob")
                .start(addNotificationBeforeClassStep())
                .next(sendNotificationBeforeClassStep())
                .build();

    }
    @Bean
    public Step sendNotificationBeforeClassStep(){
        return stepBuilderFactory.get("SendNotificationBeforeClassStep")
                .<Notification, Notification>chunk(CHUCK_SIZE)
                .reader(sendNotificationItemReader())
                .writer(sendNotificationItemWriter)   // 내부에 rest api를 호출하는 api가 있어서 클래스를 따로 생성해서 주입
                .taskExecutor(new SimpleAsyncTaskExecutor()) // taskExecutor는 멀티 스레드 방식으로 step을 실행하도록 설정하는 역할을 한다
                .build();
    }

    /**
     * 알람 정보는 계속해서 업데이트 되어야 하는데 페이징으로 처리하면 누락되는 데이터가 있을 수 있다 따라서 어쩔 수 없이 커서를 사용해야하는데
     *  커서 중 멀티 스레드 환경에서 스레드 안정성을 보장하는 것 중에 SynchronizedItemStreamReader가 있다 (동기화)
     *  하나의 스레드가 read() 메서드를 호출하면, 다른 스레드는 그 동안 기다리게 된다.
     */
    public SynchronizedItemStreamReader<Notification> sendNotificationItemReader(){
        JpaPagingItemReader<Notification> itemReader = new JpaPagingItemReaderBuilder<Notification>()
                .name("sendNotificationItemReader")
                .entityManagerFactory(entityManagerFactory) // JPA를 사용해 엔티티를 조회하기 위해 필요
                .queryString("select n from Notification n where n.event = :event and n.sent = :sent")
                .parameterValues(Map.of("event", NotificationEvent.BEFORE_CLASS, "sent", false))
                .build();

        return new SynchronizedItemStreamReaderBuilder<Notification>().delegate(itemReader).build();
        // delegate를 통해 JpaPagingItemReader를 래핑해서 원본 객체의 기능을 유지하면서 새로운 기능을 추가하거나 변경
    }

    @Bean
    public JpaPagingItemReader<Booking> addNotificationItemReader(){ // 알람 대상 가져오기 Step, 조회 대상들을 따로 업데이트할 필요가 없기 때문에 page 사용
        return new JpaPagingItemReaderBuilder<Booking>()
                .name("addNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUCK_SIZE)  // 한 번에 조회할 row 수
                // 상태(status)가 준비중이며, 시작일시(startedAt)이 10분 후 시작하는 예약을 읽어서 가져온다
                .queryString("select b from Booking b join fetch b.user where b.status = :status and b.startedAt <= :startedAt order by b.bookingSeq")
                .parameterValues(Map.of("status", BookingStatus.READY, "startedAt", LocalDateTime.now().plusMinutes(10)))
                .build();
    }

    @Bean
    public ItemProcessor<Booking, Notification> addNotificationItemProcessor(){  // 예약 엔티티를 바탕으로 알람 엔티티를 생성
        return booking -> NotificationMapStruct.INSTANCE.toNotificationEntity(booking, NotificationEvent.BEFORE_CLASS);
    }

    @Bean
    public ItemWriter<Notification> addNotificationItemWriter(){
        return new JpaItemWriterBuilder<Notification>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step addNotificationBeforeClassStep(){
        return stepBuilderFactory.get("addNotificationBeforeClassStep")
                .<Booking, Notification>chunk(CHUCK_SIZE)
                .reader(addNotificationItemReader())
                .processor(addNotificationItemProcessor())
                .writer(addNotificationItemWriter())
                .build();
    }
}
