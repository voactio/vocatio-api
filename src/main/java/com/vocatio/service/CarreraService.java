package com.vocatio.service;

import com.vocatio.dto.CarreraDetailDTO;
import com.vocatio.dto.CarreraResponseDTO;
import org.springframework.data.domain.Page;

public interface CarreraService {
    CarreraDetailDTO obtenerDetalleCarrera(Long carreraId);

    Page<CarreraResponseDTO> obtenerListadoPaginado(int page, int limit, Integer duracion, String modalidad);
}