package com.vocatio.repository;

import com.vocatio.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {
}