package com.example.bulkpass.repository;

import com.example.bulkpass.BulkPassStatus;
import com.example.bulkpass.entity.BulkPass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BulkPassRepository extends JpaRepository<BulkPass, Integer> {

    List<BulkPass> findByStatusAndStartedAtGreaterThan(BulkPassStatus status, LocalDateTime startedAt);
}
