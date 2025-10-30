package com.vocatio.repository;

import com.vocatio.model.ResultadosTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResultadosTestRepository extends JpaRepository<ResultadosTest, UUID> {
    Optional<ResultadosTest> findFirstByIdUsuarioOrderByCompletadoEnDesc(UUID idUsuario);

    // ➕ NUEVO:
    Optional<ResultadosTest> findByIdTest(UUID idTest);
}