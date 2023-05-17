package com.example.statistics.repository;

import com.example.statistics.AggregatedStatistics;
import com.example.statistics.entity.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<Statistic, Integer> {
    /**
     * 날짜별 통계 데이터 조회
     */
    @Query(value = "SELECT new com.example.statistics.AggregatedStatistics(s.statisticsAt, SUM(s.allCount), SUM(s.attendedCount), SUM(s.cancelledCount)) " +
            // 반환 타입을 Object가 아닌 AggregatedStatistics로 반환하도록 감싸준다.
            "         FROM Statistic s " +
            "        WHERE s.statisticsAt BETWEEN :from AND :to " +
            "     GROUP BY s.statisticsAt")
    List<AggregatedStatistics> findByStatisticsAtBetweenAndGroupBy(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
