package com.vocatio.service;

import com.vocatio.dto.response.CompareCareersResponse;
import com.vocatio.dto.response.CompareCareersResponse.Column;
import com.vocatio.dto.response.CompareCareersResponse.Row;
import com.vocatio.exception.ResourceNorFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareerCompareService {

    private final CarreraRepository carreraRepository;

    public CareerCompareService(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    public CompareCareersResponse compare(Long id1, Long id2) {
        Carrera c1 = carreraRepository.findById(id1)
                .orElseThrow(() -> new ResourceNorFoundException("Carrera no encontrada: " + id1));
        Carrera c2 = carreraRepository.findById(id2)
                .orElseThrow(() -> new ResourceNorFoundException("Carrera no encontrada: " + id2));

        var columns = List.of(
                new Column(String.valueOf(c1.getId()), c1.getNombre()),
                new Column(String.valueOf(c2.getId()), c2.getNombre())
        );

        var rows = List.of(
                new Row("description", "Descripción", List.of(nvl(c1.getDescripcion()), nvl(c2.getDescripcion()))),
                new Row("durationYears", "Duración (años)", List.of(c1.getDuracionAnios(), c2.getDuracionAnios())),
                new Row("modality", "Modalidad", List.of(nvl(c1.getModalidad()), nvl(c2.getModalidad()))),
                new Row("avgSalaryRange", "Salario promedio", List.of(nvl(c1.getRangoSalarioPromedio()), nvl(c2.getRangoSalarioPromedio()))),
                new Row("riasecProfile", "Perfil RIASEC", List.of(nvl(c1.getPerfilRiasec()), nvl(c2.getPerfilRiasec())))
        );

        return new CompareCareersResponse(columns, rows);
    }

    private String nvl(String s) { return s == null ? "" : s; }
}
