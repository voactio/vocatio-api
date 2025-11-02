package com.vocatio.repository;

import com.vocatio.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CarreraRepository extends JpaRepository<Carrera, Long>, JpaSpecificationExecutor<Carrera> {
}
