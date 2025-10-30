package com.vocatio.service;

import com.vocatio.dto.TestimonioResponseDTO;

import java.util.List;

public interface TestimonioService {
    List<TestimonioResponseDTO> obtenerTestimoniosPorCarrera(Long carreraId);
}

