package com.vocatio.service;

import com.vocatio.dto.request.CompareCareersRequest;
import com.vocatio.dto.response.CompareCareersResponse;
import com.vocatio.exception.BadRequestException;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.ResultadosTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareerCompareService {

    private final ResultadosTestRepository resultadosTestRepository;
    private final CarreraRepository carreraRepository;

    public CompareCareersResponse comparar(CompareCareersRequest request) {

        UUID idUsuario   = request.getIdUsuario();
        Long idResultado = request.getIdResultado();

        log.info("Comparando carreras - Usuario: {}, Resultado: {}, Carrera1: {}, Carrera2: {}",
                idUsuario, idResultado, request.getIdCarrera1(), request.getIdCarrera2());

        // 1. Verificar que el resultado existe y pertenece al usuario
        var resultado = resultadosTestRepository.findByIdAndIdUsuario(idResultado, idUsuario)
                .orElseThrow(() -> new BadRequestException("No se encontró el resultado del test para este usuario."));

        log.info("Resultado encontrado: {}", resultado.getId());

        // 2. top 5 real según tu SQL
        List<Long> top5 = resultadosTestRepository
                .findTop5CarrerasByResultadoAndUsuario(idResultado, idUsuario);

        log.info("Top 5 carreras encontradas: {}", top5);

        if (top5.isEmpty()) {
            throw new BadRequestException("No hay ranking de carreras para ese resultado.");
        }

        // 3. validar que las dos estén ahí
        if (!top5.contains(request.getIdCarrera1())) {
            log.error("Carrera1 {} no está en el top 5: {}", request.getIdCarrera1(), top5);
            throw new BadRequestException("La primera carrera seleccionada no está en tu ranking (top 5).");
        }

        if (!top5.contains(request.getIdCarrera2())) {
            log.error("Carrera2 {} no está en el top 5: {}", request.getIdCarrera2(), top5);
            throw new BadRequestException("La segunda carrera seleccionada no está en tu ranking (top 5).");
        }

        // 4. cargar carreras
        Carrera c1 = carreraRepository.findById(request.getIdCarrera1())
                .orElseThrow(() -> new ResourceNotFoundException("Carrera 1 no encontrada"));
        Carrera c2 = carreraRepository.findById(request.getIdCarrera2())
                .orElseThrow(() -> new ResourceNotFoundException("Carrera 2 no encontrada"));

        log.info("Carreras cargadas: {} y {}", c1.getNombre(), c2.getNombre());

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