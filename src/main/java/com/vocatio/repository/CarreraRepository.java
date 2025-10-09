package com.vocatio.repository;

import com.vocatio.model.Carrera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    Page<Carrera> findByDuracionAnios(Integer duracionAnios, Pageable pageable);
    Page<Carrera> findByModalidadIgnoreCase(String modalidad, Pageable pageable);
    Page<Carrera> findByDuracionAniosAndModalidadIgnoreCase(Integer duracionAnios, String modalidad, Pageable pageable);
}

