package com.vocatio.service;

import com.vocatio.dto.request.UniversitiesByCareerRequest;
import com.vocatio.dto.response.UniversitiesByCareerResponse;
import com.vocatio.exception.BadRequestException;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.model.UniversidadCarrera;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.UniversidadCarreraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversidadService {

    private final CarreraRepository carreraRepository;
    private final UniversidadCarreraRepository universidadCarreraRepository;

    public UniversitiesByCareerResponse obtener(UniversitiesByCareerRequest request) {

        if (request.getIdCarrera() == null || request.getIdCarrera() <= 0) {
            throw new BadRequestException("Debes enviar un idCarrera válido.");
        }

        Carrera carrera = carreraRepository.findById(request.getIdCarrera())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la carrera indicada."));

        List<UniversidadCarrera> relaciones = universidadCarreraRepository
                .findAllByCarrera(request.getIdCarrera());

        if (relaciones.isEmpty()) {
            throw new ResourceNotFoundException("No hay universidades asociadas a esta carrera.");
        }

        var items = relaciones.stream()
                .map(uc -> UniversitiesByCareerResponse.ItemUniversidad.builder()
                        .idUniversidad(uc.getUniversidad().getId())
                        .nombreUniversidad(uc.getUniversidad().getNombre())
                        .ubicacion(uc.getUniversidad().getUbicacion())
                        .duracionAnios(carrera.getDuracionAnios())
                        .costoPorAnio(uc.getCostoPorAnio())
                        .urlUniversidad(uc.getUniversidad().getUrlSitioWeb())
                        .urlPlanEspecifico(uc.getUrlPlanEspecifico())
                        .build()
                )
                .toList();

        return UniversitiesByCareerResponse.builder()
                .idCarrera(carrera.getId())
                .nombreCarrera(carrera.getNombre())
                .universidades(items)
                .build();
    }
}
