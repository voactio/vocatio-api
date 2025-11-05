package com.vocatio.repository;

import com.vocatio.model.AreaInteres;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AreaInteresRepository extends JpaRepository<AreaInteres, Long> {
    Optional<AreaInteres> findByNombre(String nombre);
}