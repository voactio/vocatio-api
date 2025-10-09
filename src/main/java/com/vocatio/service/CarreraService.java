package com.vocatio.service;

import com.vocatio.dto.CarreraResponseDTO;
import org.springframework.data.domain.Page;

public interface CarreraService {
    Page<CarreraResponseDTO> obtenerListadoPaginado(Integer page, Integer limit, Integer duracion, String modalidad);
}

