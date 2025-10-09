package com.vocatio.service.impl;

import com.vocatio.dto.CarreraResponseDTO;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.service.CarreraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CarreraServiceImpl implements CarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraServiceImpl(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @Override
    public Page<CarreraResponseDTO> obtenerListadoPaginado(Integer page, Integer limit, Integer duracion, String modalidad) {
        int safePage = page != null && page >= 0 ? page : 0;
        int safeLimit = (limit != null && limit > 0 && limit <= 200) ? limit : 10;
        Pageable pageable = PageRequest.of(safePage, safeLimit, Sort.by("nombre").ascending());

        Page<Carrera> pageResult;
        if (duracion != null && modalidad != null && !modalidad.isBlank()) {
            pageResult = carreraRepository.findByDuracionAniosAndModalidadIgnoreCase(duracion, modalidad, pageable);
        } else if (duracion != null) {
            pageResult = carreraRepository.findByDuracionAnios(duracion, pageable);
        } else if (modalidad != null && !modalidad.isBlank()) {
            pageResult = carreraRepository.findByModalidadIgnoreCase(modalidad, pageable);
        } else {
            pageResult = carreraRepository.findAll(pageable);
        }

        return pageResult.map(c -> new CarreraResponseDTO(c.getId(), c.getNombre(), c.getDescripcion()));
    }
}

