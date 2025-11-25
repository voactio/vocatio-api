package com.vocatio.repository;

import com.vocatio.model.ResultadoCarrera;
import com.vocatio.model.ResultadosTest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultadoCarreraRepository extends JpaRepository<ResultadoCarrera, Long> {
    List<ResultadoCarrera> findByResultadoTestOrderByOrdenAsc(ResultadosTest resultadoTest);
}