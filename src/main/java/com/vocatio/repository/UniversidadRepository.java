package com.vocatio.repository;

import com.vocatio.model.Universidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversidadRepository extends JpaRepository<Universidad, Long> {
}