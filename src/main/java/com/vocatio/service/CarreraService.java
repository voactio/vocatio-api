package com.vocatio.service;

import com.vocatio.dto.CarreraResponseDTO;
import org.springframework.data.domain.Page;

public interface CarreraService {
    Page<CarreraResponseDTO> obtenerListadoPaginado(int page, int limit);
}

