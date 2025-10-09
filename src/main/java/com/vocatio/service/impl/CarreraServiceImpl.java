package com.vocatio.service.impl;

import com.vocatio.dto.CarreraDetailDTO;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.service.CarreraService;
import org.springframework.stereotype.Service;

@Service
public class CarreraServiceImpl implements CarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraServiceImpl(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @Override
    public CarreraDetailDTO obtenerDetalleCarrera(Long carreraId) {
        Carrera carrera = carreraRepository.findById(carreraId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrera no encontrada con id: " + carreraId));
        return mapToDetailDTO(carrera);
    }

    private CarreraDetailDTO mapToDetailDTO(Carrera c) {
        return new CarreraDetailDTO(
                c.getId(),
                c.getNombre(),
                c.getDuracionAnios(),
                c.getModalidad(),
                c.getDescripcion(),
                c.getPlanEstudios(),
                c.getUniversidadesSugeridas()
        );
    }
}

