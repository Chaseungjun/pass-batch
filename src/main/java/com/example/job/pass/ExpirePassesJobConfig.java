package com.example.job.pass;

import com.example.pass.PassStatus;
import com.example.pass.entity.Pass;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ExpirePassesJobConfig {  // 이용권 만료

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final int CHUNK_SIZE = 5;

    @Bean
    public Job expirePassJob() {
        return jobBuilderFactory.get("expirePassJob")
                .start(expirePassStep())
                .build();
    }

    @Bean
    public Step expirePassStep() {
        return stepBuilderFactory.get("expirePassStep")
                .<Pass, Pass>chunk(CHUNK_SIZE)
                .reader(expirePassesItemReader())
                .processor(expirePassesItemProcessor())
                .writer(expirePassesItemWriter())
                .build();
    }

    @Bean
    @StepScope // step실행 시 객체가 매번 생성되어 스레드마다 할당
    public JpaCursorItemReader<Pass> expirePassesItemReader() {
        return new JpaCursorItemReaderBuilder<Pass>()
                .name("expirePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                // 상태(status)가 진행중이며, 종료일시(endedAt)이 현재 시점보다 과거일 경우 만료 대상
                .queryString("select p from Pass p where p.status = :status and p.endedAt <= :endedAt")
                // 상태가 진행중인것만 하나씩 체크하여 읽어오므로 페이징이 아닌 커서기법을 사용
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<Pass, Pass> expirePassesItemProcessor(){
        return pass ->{  // pass는 JpaCursorItemReader로 읽은 값
            pass.updateStatusAndExpiredAt(PassStatus.EXPIRED, LocalDateTime.now());
            return pass;
        };
    }

    @Bean
    public ItemWriter<Pass> expirePassesItemWriter(){
        return new JpaItemWriterBuilder<Pass>()
                .entityManagerFactory(entityManagerFactory)  // jpa의 영속성 관리를 위해 entityManagerFactory설정
                .build();
    }

}
