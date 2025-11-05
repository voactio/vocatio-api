package com.vocatio.repository;

import com.vocatio.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    @Query(value = """
            SELECT r.*
            FROM recursos r
            JOIN carrera_recursos cr ON cr.id_recurso = r.id
            WHERE cr.id_carrera = :idCarrera
            """, nativeQuery = true)
    List<Recurso> findByCarrera(@Param("idCarrera") Long idCarrera);
}
