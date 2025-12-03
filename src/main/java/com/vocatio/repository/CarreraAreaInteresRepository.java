// CarreraAreaInteresRepository.java
package com.vocatio.repository;

import com.vocatio.model.CarreraAreaInteres;
import com.vocatio.model.CarreraAreaInteresId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CarreraAreaInteresRepository extends JpaRepository<CarreraAreaInteres, CarreraAreaInteresId> {

    // Con @IdClass, estos métodos funcionan directamente
    List<CarreraAreaInteres> findByIdCarrera(Long carreraId);

    List<CarreraAreaInteres> findByIdAreaInteres(Long areaInteresId);

    @Query("SELECT cai FROM CarreraAreaInteres cai WHERE cai.idCarrera = :carreraId ORDER BY cai.puntajeRelevancia DESC")
    List<CarreraAreaInteres> findByCarreraIdOrderByPuntajeRelevanciaDesc(@Param("carreraId") Long carreraId);

    @Query("SELECT cai FROM CarreraAreaInteres cai WHERE cai.idAreaInteres IN :areaIds")
    List<CarreraAreaInteres> findByAreaInteresIdIn(@Param("areaIds") Set<Long> areaIds);
}