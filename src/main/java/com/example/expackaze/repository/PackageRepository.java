package com.example.expackaze.repository;

import com.example.expackaze.entity.Expackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Expackage, Long> {
}
