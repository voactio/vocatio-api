package com.vocatio.repository;

import com.vocatio.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Optional<Report> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}