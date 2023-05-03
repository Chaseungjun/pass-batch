package com.example.pass.repository;

import com.example.pass.entity.Pass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PassRepository extends JpaRepository<Pass, Integer> {

    @Transactional
    @Modifying
    @Query("update Pass p" +
            " set p.remainingCount = :remainingCount," +
            " p.modifiedAt = current_timestamp" +
            " where p.passSeq =:passSeq")
    int updateRemainingCount(Integer passSeq, Integer remainingCount);
}
