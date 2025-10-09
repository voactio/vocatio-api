package com.vocatio.repository;

import com.vocatio.model.Carrera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    // Declarado explícitamente para claridad (ya existe en JpaRepository)
    Page<Carrera> findAll(Pageable pageable);
}

