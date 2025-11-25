package com.vocatio.repository;

import com.vocatio.model.ResultadosTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResultadosTestRepository extends JpaRepository<ResultadosTest, Long> {

    Optional<ResultadosTest> findByIdAndIdUsuario(Long id, UUID idUsuario);

    @Query(value = """
        WITH resultados AS (
            SELECT
                rt.id         AS resultado_id,
                rt.id_usuario AS usuario_id,
                rt.puntajes   AS puntajes_json
            FROM resultados_test rt
            WHERE rt.id = :idResultado
              AND rt.id_usuario = :idUsuario
        ),
        carreras_scores AS (
            SELECT
                r.usuario_id,
                r.resultado_id,
                c.id    AS carrera_id,
                SUM(
                    COALESCE( (r.puntajes_json ->> ai.nombre)::int, 0 )
                    * COALESCE(cai.puntaje_relevancia, 1)
                ) AS score
            FROM resultados r
            CROSS JOIN carreras c
            LEFT JOIN carrera_areas_interes cai
                ON cai.id_carrera = c.id
            LEFT JOIN areas_interes ai
                ON ai.id = cai.id_area_interes
            GROUP BY
                r.usuario_id,
                r.resultado_id,
                c.id
        ),
        ranked AS (
            SELECT
                cs.*,
                ROW_NUMBER() OVER (
                    PARTITION BY cs.usuario_id, cs.resultado_id
                    ORDER BY cs.score DESC, cs.carrera_id ASC
                ) AS rn
            FROM carreras_scores cs
        )
        SELECT carrera_id
        FROM ranked
        WHERE rn <= 5
        ORDER BY rn
        """, nativeQuery = true)
    List<Long> findTop5CarrerasByResultadoAndUsuario(
            @Param("idResultado") Long idResultado,
            @Param("idUsuario") UUID idUsuario
    );
    List<ResultadosTest> findByIdUsuarioOrderByCompletadoEnDesc(UUID idUsuario);
}
