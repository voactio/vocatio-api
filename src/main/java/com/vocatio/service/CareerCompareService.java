package com.vocatio.service;

import com.vocatio.dto.request.CompareCareersRequest;
import com.vocatio.dto.response.CompareCareersResponse;
import com.vocatio.exception.BadRequestException;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.ResultadosTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CareerCompareService {

    private final ResultadosTestRepository resultadosTestRepository;
    private final CarreraRepository carreraRepository;

    public CompareCareersResponse comparar(CompareCareersRequest request) {

        UUID idUsuario   = request.getIdUsuario();
        Long idResultado = request.getIdResultado();

        // 1. top 5 real según tu SQL
        List<Long> top5 = resultadosTestRepository
                .findTop5CarrerasByResultadoAndUsuario(idResultado, idUsuario);

        if (top5.isEmpty()) {
            throw new BadRequestException("No hay ranking de carreras para ese resultado.");
        }

        // 2. validar que las dos estén ahí
        if (!top5.contains(request.getIdCarrera1()) ||
                !top5.contains(request.getIdCarrera2())) {
            throw new BadRequestException("Solo puedes comparar carreras que salieron en tu ranking (top 5) de ese resultado.");
        }

        // 3. cargar carreras
        Carrera c1 = carreraRepository.findById(request.getIdCarrera1())
                .orElseThrow(() -> new ResourceNotFoundException("Carrera 1 no encontrada"));
        Carrera c2 = carreraRepository.findById(request.getIdCarrera2())
                .orElseThrow(() -> new ResourceNotFoundException("Carrera 2 no encontrada"));

        var item1 = CompareCareersResponse.CareerCompareItem.builder()
                .id(c1.getId())
                .nombre(c1.getNombre())
                .descripcion(c1.getDescripcion())
                .DuracionAnios(c1.getDuracionAnios())
                .modalidad(c1.getModalidad())
                .rangoSalarioPromedio(c1.getRangoSalarioPromedio())
                .areaInteres(null)
                .build();

        var item2 = CompareCareersResponse.CareerCompareItem.builder()
                .id(c2.getId())
                .nombre(c2.getNombre())
                .descripcion(c2.getDescripcion())
                .DuracionAnios(c2.getDuracionAnios())
                .modalidad(c2.getModalidad())
                .rangoSalarioPromedio(c2.getRangoSalarioPromedio())
                .areaInteres(null)
                .build();

        return CompareCareersResponse.builder()
                .carrera1(item1)
                .carrera2(item2)
                .build();
    }
}
