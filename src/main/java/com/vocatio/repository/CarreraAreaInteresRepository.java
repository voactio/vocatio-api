package com.vocatio.repository;

import com.vocatio.model.CarreraAreaInteres;
import com.vocatio.model.CarreraAreaInteresId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarreraAreaInteresRepository extends JpaRepository<CarreraAreaInteres, CarreraAreaInteresId> {

    List<CarreraAreaInteres> findByIdAreaInteresIn(Iterable<Long> idsAreas);
}