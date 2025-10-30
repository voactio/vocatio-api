package com.vocatio.repository;

import com.vocatio.model.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestSessionRepository extends JpaRepository<TestSession, Long> {
}