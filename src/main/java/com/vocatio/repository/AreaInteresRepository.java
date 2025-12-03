package com.vocatio.repository;

import com.vocatio.model.AreaInteres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaInteresRepository extends JpaRepository<AreaInteres, Long> {
    Optional<AreaInteres> findByNombre(String nombre);
}