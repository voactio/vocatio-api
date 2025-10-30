package com.vocatio.service.impl;

import com.vocatio.dto.TestimonioResponseDTO;
import com.vocatio.model.Testimonio;
import com.vocatio.repository.TestimonioRepository;
import com.vocatio.service.TestimonioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestimonioServiceImpl implements TestimonioService {

    private final TestimonioRepository testimonioRepository;

    public TestimonioServiceImpl(TestimonioRepository testimonioRepository) {
        this.testimonioRepository = testimonioRepository;
    }

    @Override
    public List<TestimonioResponseDTO> obtenerTestimoniosPorCarrera(Long carreraId) {
        List<Testimonio> items = testimonioRepository.findAllByCarreraId(carreraId);
        return items.stream()
                .map(t -> new TestimonioResponseDTO(t.getId(), t.getContenido(), t.getNombreAutor()))
                .collect(Collectors.toList());
    }
}

