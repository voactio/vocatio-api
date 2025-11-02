package com.vocatio.service;

import com.vocatio.dto.response.CarreraCardResponse;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraService(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    // ... métodos existentes (detalle, filtrar, etc.)

    public List<CarreraCardResponse> listarInicial(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "nombre"));
        return carreraRepository.findAll(pageable)
                .map(this::toCard)
                .getContent();
    }

    private CarreraCardResponse toCard(Carrera c) {
        return new CarreraCardResponse(
                c.getId(),
                nullSafe(c.getNombre()),
                shorten(nullSafe(c.getDescripcion()), 160)
        );
    }

    private String nullSafe(String s) { return s == null ? "" : s; }

    private String shorten(String s, int max) {
        String flat = s.replaceAll("\\s+", " ").trim();
        if (flat.length() <= max) return flat;
        return flat.substring(0, Math.max(0, max - 1)).trim() + "…";
    }
}
