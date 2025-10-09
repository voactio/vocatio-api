package com.vocatio.repository;

import com.vocatio.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    Optional<Pregunta> findByTestIdAndOrden(Long testId, int orden);
}