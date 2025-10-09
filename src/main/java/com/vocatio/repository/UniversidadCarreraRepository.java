package com.vocatio.repository;


import com.vocatio.model.UniversidadCarreraLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface UniversidadCarreraRepository
        extends JpaRepository<UniversidadCarreraLink, UniversidadCarreraLink.Id> {

    interface OfferRow {
        Long getId();                  // u.id
        String getName();              // u.nombre
        String getCity();              // u.ubicacion
        Integer getProgramDurationYears(); // c.duracion_anios
        String getType();              // u.tipo
        String getMoreInfoUrl();       // uc.url_plan_especifico
        BigDecimal getAnnualCost();    // uc.costo_por_ano
    }

    @Query(value = """
        SELECT
          u.id                    AS id,
          u.nombre                AS name,
          u.ubicacion             AS city,
          c.duracion_anios        AS programDurationYears,
          u.tipo                  AS type,
          uc.url_plan_especifico  AS moreInfoUrl,
          uc.costo_por_ano        AS annualCost
        FROM universidad_carreras uc
        JOIN universidades u ON u.id = uc.id_universidad_bigint
        JOIN carreras c       ON c.id = uc.id_carrera_bigint
        WHERE uc.id_carrera_bigint = :careerId
        ORDER BY u.nombre
    """, nativeQuery = true)
    List<OfferRow> findOffersByCareer(@Param("careerId") Long careerId);
}
