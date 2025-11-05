package com.vocatio.repository;


import com.vocatio.model.UniversidadCarrera;
import com.vocatio.model.UniversidadCarreraId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface UniversidadCarreraRepository extends JpaRepository<UniversidadCarrera, UniversidadCarreraId> {

    @Query(value = """
        SELECT *
        FROM universidad_carreras uc
        JOIN universidades u ON u.id = uc.id_universidad
        JOIN carreras c ON c.id = uc.id_carrera
        WHERE uc.id_carrera = :idCarrera
        """, nativeQuery = true)
    List<UniversidadCarrera> findAllByCarrera(@Param("idCarrera") Long idCarrera);
}
