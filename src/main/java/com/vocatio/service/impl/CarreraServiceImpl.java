package com.vocatio.service.impl;

import com.vocatio.dto.CarreraDetailDTO;
import com.vocatio.dto.CarreraResponseDTO;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.service.CarreraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarreraServiceImpl implements CarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraServiceImpl(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CarreraDetailDTO obtenerDetalleCarrera(Long carreraId) {
        Carrera carrera = carreraRepository.findById(carreraId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrera no encontrada con id: " + carreraId));
        return mapToDetailDTO(carrera);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarreraResponseDTO> obtenerListadoPaginado(int page, int limit) {
        int safePage = Math.max(page, 0);
        int safeLimit = limit > 0 ? limit : 10;
        Pageable pageable = PageRequest.of(safePage, safeLimit, Sort.by(Sort.Direction.ASC, "nombre"));
        return carreraRepository.findAll(pageable)
                .map(this::toDto);
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

    private CarreraResponseDTO toDto(Carrera c) {
        return CarreraResponseDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .build();
    }
}