package com.example.pass.repository;

import com.example.pass.entity.Pass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassRepository extends JpaRepository<Pass, Integer> {
}
