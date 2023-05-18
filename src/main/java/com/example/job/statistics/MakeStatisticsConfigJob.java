package com.example.job.statistics;

import com.example.booking.entity.Booking;
import com.example.statistics.entity.Statistic;
import com.example.statistics.repository.StatisticRepository;
import com.example.util.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MakeStatisticsConfigJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final StatisticRepository statisticRepository;
    private final MakeDailyStatisticsTasklet makeDailyStatisticsTasklet;
    private final MakeWeeklyStatisticsTasklet makeWeeklyStatisticsTasklet;

    private final int CHUCK_SIZE = 10;

    /**
     * jobBuilderFactory 만으로도 step의 실행순서를 정할 수 있지만
     * Flow를 사용하면 job 내에서 step 간의 병렬 처리, 조건부 실행, 다양한 제어 흐름 등을 쉽게 구현할 수 있습니다.
     */
    @Bean
    public Job makeStatisticsJob() {
        Flow addStatisticsFlow = new FlowBuilder<Flow>("addStatisticsFlow")
                .start(addStatisticsStep())
                .build();

        Flow makeDailyStatisticsFlow = new FlowBuilder<Flow>("makeDailyStatisticsFlow")
                .start(makeDailyStatisticsStep())
                .build();

        Flow makeWeeklyStatisticsFlow = new FlowBuilder<Flow>("makeWeeklyStatisticsFlow")
                .start(makeWeeklyStatisticsStep())
                .build();

        Flow parallelMakeStatisticsFlow = new FlowBuilder<Flow>("parallelMakeStatisticsFlow")
                .split(new SimpleAsyncTaskExecutor())
                // split은 FlowBuilder에서 병렬처리를 하기위한 기능으로 SimpleAsyncTaskExecutor를 사용하여 병렬처리한다.
                // 병렬처리되는 모든 작업이 완료될 때까지 대기한다
                .add(makeDailyStatisticsFlow, makeWeeklyStatisticsFlow)
                .build();

        return this.jobBuilderFactory.get("makeStatisticsJob")
                .start(addStatisticsFlow)
                .next(parallelMakeStatisticsFlow)
                .build()
                .build();
    }

    @Bean
    public Step addStatisticsStep(){
        return stepBuilderFactory.get("addStatisticsStep")
                .<Booking, Booking>chunk(CHUCK_SIZE)
                .reader(addStatisticsItemReader(null, null))
                .writer(addStatisticsItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<Booking> addStatisticsItemReader(@Value("#{jobParameters[from]}") String fromString,
                                                                @Value("#{jobParameters[to]}") String toString) {
        // 파라미터로 입력받은 날짜를 YYYY_MM_DD_HH_MM 형태로 변환
        final LocalDateTime from = LocalDateTimeUtils.parse(fromString);
        final LocalDateTime to = LocalDateTimeUtils.parse(toString);

        /**
         * JobParameter를 받아 종료 일시(endedAt) 기준으로 통계 대상 예약(Booking)을 조회합니다.
         * 시작일시를 기준으로 조회할 경우 예약이 중복되어 계산될 수도 있고, 중간에 예약이 취소된다면 실제 결과와 차이가 발생할 수 있습니다
         */
        return new JpaCursorItemReaderBuilder<Booking>()
                .name("usePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from BookingEntity b where b.endedAt between :from and :to")
                .parameterValues(Map.of("from", from, "to", to))
                .build();
    }

    /**
     * Booking 리스트를 여러 날짜별로 그룹핑하여 통계 정보를 계산하고, 이를 Statistics로 변환하여 DB에 저장하는 역할
     */
    @Bean
    public ItemWriter<Booking> addStatisticsItemWriter() {
        return bookingEntities -> {
            // LinkedHashMap은 삽입순서를 보장하기 때문에 날짜 순서대로 통계데이터를 저장할 수 있다
                Map<LocalDateTime, Statistic> statisticsEntityMap = new LinkedHashMap<>();

            for (Booking bookingEntity : bookingEntities) {
                final LocalDateTime statisticsAt = bookingEntity.getStatisticsAt(); // 예약 날짜 정보를 가지고온다.(일 기준)
                Statistic statisticsEntity = statisticsEntityMap.get(statisticsAt); // 해당하는 날짜의 statistic 객체를 찾는다

                if (statisticsEntity == null) {
                    statisticsEntityMap.put(statisticsAt, Statistic.create(bookingEntity));
                    // 처음에는 아무것도 없으므로 예약데이터 생성하고 카운트

                } else {
                    statisticsEntity.add(bookingEntity);
                }
            }
            final List<Statistic> statisticsEntities = new ArrayList<>(statisticsEntityMap.values());
            //맵에 추가된 모든 StatisticsEntity 객체를 리스트로 변환하고 저장
            statisticRepository.saveAll(statisticsEntities);
            log.info("### addStatisticsStep 종료");

        };
    }

    @Bean  // 스텝 2개를 병렬로 처리
    public Step makeDailyStatisticsStep() {
        return this.stepBuilderFactory.get("makeDailyStatisticsStep")
                .tasklet(makeDailyStatisticsTasklet)
                .build();
    }

    @Bean
    public Step makeWeeklyStatisticsStep() {
        return this.stepBuilderFactory.get("makeWeeklyStatisticsStep")
                .tasklet(makeWeeklyStatisticsTasklet)
                .build();
    }

}
