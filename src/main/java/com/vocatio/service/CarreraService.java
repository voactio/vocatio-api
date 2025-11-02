// src/main/java/com/vocatio/service/CarreraService.java
package com.vocatio.service;

import com.vocatio.dto.response.CarreraDetailResponse;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraService(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    public CarreraDetailResponse obtenerDetalleCarrera(Long carreraId) {
        Carrera c = carreraRepository.findById(carreraId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada"));
        return new CarreraDetailResponse(
                c.getId(),
                c.getNombre(),
                c.getDescripcion(),
                c.getDuracionAnios(),
                c.getModalidad(),
                c.getRangoSalarioPromedio(),
                c.getCreadoEn(),
                c.getActualizadoEn()
        );
    }
}