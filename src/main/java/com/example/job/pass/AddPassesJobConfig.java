package com.example.job.pass;

import com.example.pass.AddPassesTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class AddPassesJobConfig {       // 이용권 일괄지급

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final AddPassesTasklet addPassesTasklet;
    private final EntityManagerFactory entityManagerFactory;

    public Job addPassesJob(){
       return jobBuilderFactory.get("addPassesJob")
                .start(addPassesStep())
                .build();
    }

    public Step addPassesStep(){
        return stepBuilderFactory.get("addPassesStep")
                .tasklet(addPassesTasklet)
                .build();
    }




}
