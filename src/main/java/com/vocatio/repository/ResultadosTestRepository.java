package com.vocatio.repository;

import com.vocatio.model.ResultadosTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultadosTestRepository extends JpaRepository<ResultadosTest, Long> {

    Optional<ResultadosTest> findByIdAndIdUsuario(Long id, Long idUsuario);
}