package com.vocatio.service;

import com.vocatio.dto.response.RecursoResponse;
import com.vocatio.exception.BadRequestException;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.model.Recurso;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.RecursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecursosService {

    private final CarreraRepository carreraRepository;
    private final RecursoRepository recursoRepository;

    public List<RecursoResponse> listarPorCarrera(Long idCarrera) {

        // 1. validar entrada
        if (idCarrera == null || idCarrera <= 0) {
            throw new BadRequestException("El id de la carrera no es válido.");
        }

        // 2. asegurar que la carrera existe
        Carrera carrera = carreraRepository.findById(idCarrera)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la carrera indicada."));

        // 3. obtener recursos asociados
        List<Recurso> recursos = recursoRepository.findByCarrera(carrera.getId());

        if (recursos.isEmpty()) {
            throw new ResourceNotFoundException("La carrera no tiene recursos digitales registrados.");
        }

        // 4. mapear a DTO
        return recursos.stream()
                .map(r -> RecursoResponse.builder()
                        .titulo(r.getTitulo())
                        .tipoRecurso(r.getTipoRecurso())
                        .autor(r.getAutor())
                        .url(r.getUrl())
                        .build())
                .toList();
    }
}
