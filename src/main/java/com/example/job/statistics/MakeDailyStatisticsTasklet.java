package com.example.job.statistics;


import com.example.statistics.AggregatedStatistics;
import com.example.statistics.repository.StatisticRepository;
import com.example.util.CustomCSVWriter;
import com.example.util.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class MakeDailyStatisticsTasklet implements Tasklet {

    private final StatisticRepository statisticRepository;

    @Value("#{jobParameters[from]}")  // 입력받은 파라미터 값
    private String fromString;
    @Value("#{jobParameters[to]}")
    private String toString;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        final LocalDateTime from = LocalDateTimeUtils.parse(fromString);
        final LocalDateTime to = LocalDateTimeUtils.parse(toString);

        // 날짜별 통계데이터
        final List<AggregatedStatistics> statisticsList = statisticRepository.findByStatisticsAtBetweenAndGroupBy(from, to);

        /**
         * 예시 (csv 파일로 다음과 같이 생성된다)
         * statisticsAt        allCount    attendedCount    cancelledCount
         * 2023-01-01          10              8                   2
         * 2023-01-02          15             12                   3
         * 2023-01-03          20             18                   2
         */
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"statisticsAt", "allCount", "attendedCount", "cancelledCount"});
        for (AggregatedStatistics statistics : statisticsList) {
            data.add(new String[]{
                    LocalDateTimeUtils.format(statistics.getStatisticsAt()),
                    String.valueOf(statistics.getAllCount()),
                    String.valueOf(statistics.getAttendedCount()),
                    String.valueOf(statistics.getCancelledCount())
            });
        }
        CustomCSVWriter.write("daily_statistics_" + LocalDateTimeUtils.format(from, LocalDateTimeUtils.YYYY_MM_DD) + ".csv", data);
        return RepeatStatus.FINISHED;
    }
}
