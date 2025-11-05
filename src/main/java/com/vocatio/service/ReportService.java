package com.vocatio.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocatio.dto.response.CarreraAfinDto;
import com.vocatio.dto.response.GenerateReportResponse;
import com.vocatio.exception.ResourceNorFoundException;
import com.vocatio.model.*;
import com.vocatio.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ResultadosTestRepository resultadoTestRepository;
    private final AreaInteresRepository areaInteresRepository;
    private final CarreraAreaInteresRepository carreraAreaInteresRepository;
    private final CarreraRepository carreraRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Lee el resultado, cruza con áreas y carreras y devuelve todo listo
     * para pintarlo o para generar el PDF.
     */
    public GenerateReportResponse obtenerResultadoConRecomendaciones(Long idResultado, UUID idUsuario) {
        // 1. buscar resultado y validar que sea de ese usuario
        ResultadosTest resultado = resultadoTestRepository
                .findByIdAndIdUsuario(idResultado, idUsuario)
                .orElseThrow(() -> new ResourceNorFoundException("Resultado no encontrado o no pertenece al usuario"));

        // 2. convertir el jsonb a Map<String, Integer>
        Map<String, Integer> puntajesPorArea = parsearPuntajes(resultado.getPuntajes());

        if (puntajesPorArea.isEmpty()) {
            return GenerateReportResponse.builder()
                    .idResultado(resultado.getId())
                    .completadoEn(resultado.getCompletadoEn())
                    .puntajes(Collections.emptyMap())
                    .topCarreras(Collections.emptyList())
                    .build();
        }

        // 3. obtener todas las áreas de la BD para mapear nombre -> id
        List<AreaInteres> todasLasAreas = areaInteresRepository.findAll();
        Map<String, Long> areaNombreToId = todasLasAreas.stream()
                .collect(Collectors.toMap(AreaInteres::getNombre, AreaInteres::getId));

        // 4. de los puntajes, quedarnos solo con las áreas que existen en BD
        Map<Long, Integer> areaIdToPuntaje = new HashMap<>();
        for (Map.Entry<String, Integer> entry : puntajesPorArea.entrySet()) {
            String nombreArea = entry.getKey();
            Integer puntaje = entry.getValue();
            Long idArea = areaNombreToId.get(nombreArea);
            if (idArea != null) {
                areaIdToPuntaje.put(idArea, puntaje);
            }
        }

        if (areaIdToPuntaje.isEmpty()) {
            return GenerateReportResponse.builder()
                    .idResultado(resultado.getId())
                    .completadoEn(resultado.getCompletadoEn())
                    .puntajes(puntajesPorArea)
                    .topCarreras(Collections.emptyList())
                    .build();
        }

        // 5. buscar todas las relaciones carrera-area para esas áreas
        List<CarreraAreaInteres> relaciones = carreraAreaInteresRepository.findByIdAreaInteresIn(areaIdToPuntaje.keySet());

        // 6. acumular puntaje por carrera
        //    fórmula muy simple: scoreCarrera += puntajeAreaUsuario * (relevancia o 1)
        Map<Long, Double> carreraToScore = new HashMap<>();
        // también guardamos qué área aportó más a esa carrera, para mostrarla
        Map<Long, Long> carreraToBestArea = new HashMap<>();
        Map<Long, Integer> carreraToBestAreaScore = new HashMap<>();

        for (CarreraAreaInteres rel : relaciones) {
            Long idCarrera = rel.getIdCarrera();
            Long idArea = rel.getIdAreaInteres();
            Integer puntajeUsuarioEnArea = areaIdToPuntaje.getOrDefault(idArea, 0);
            int relevancia = rel.getPuntajeRelevancia() != null ? rel.getPuntajeRelevancia() : 1;

            double aporte = puntajeUsuarioEnArea * relevancia;

            carreraToScore.merge(idCarrera, (double) aporte, Double::sum);

            // para guardar el área principal de esa carrera
            int best = carreraToBestAreaScore.getOrDefault(idCarrera, -1);
            if (puntajeUsuarioEnArea > best) {
                carreraToBestArea.put(idCarrera, idArea);
                carreraToBestAreaScore.put(idCarrera, puntajeUsuarioEnArea);
            }
        }

        // 7. traer las carreras que obtuvieron puntaje
        List<Long> idsCarreras = new ArrayList<>(carreraToScore.keySet());
        Map<Long, Carrera> carreraMap = carreraRepository.findAllById(idsCarreras)
                .stream()
                .collect(Collectors.toMap(Carrera::getId, c -> c));

        // 8. ordenar por score desc y quedarnos con las 5 primeras
        List<Map.Entry<Long, Double>> topEntries = carreraToScore.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .toList();

        // 9. armar DTO de top carreras
        List<CarreraAfinDto> topCarreras = new ArrayList<>();
        // normalizar a porcentaje respecto a la más alta
        double maxScore = topEntries.isEmpty() ? 0.0 : topEntries.get(0).getValue();

        for (Map.Entry<Long, Double> entry : topEntries) {
            Long idCarrera = entry.getKey();
            Double score = entry.getValue();
            Carrera carrera = carreraMap.get(idCarrera);
            if (carrera == null) continue;

            Long idAreaPrincipal = carreraToBestArea.get(idCarrera);
            String nombreArea = null;
            if (idAreaPrincipal != null) {
                nombreArea = todasLasAreas.stream()
                        .filter(a -> a.getId().equals(idAreaPrincipal))
                        .map(AreaInteres::getNombre)
                        .findFirst()
                        .orElse(null);
            }

            double porcentaje = (maxScore > 0) ? (score / maxScore) * 100.0 : 0.0;

            topCarreras.add(
                    CarreraAfinDto.builder()
                            .id(carrera.getId())
                            .nombre(carrera.getNombre())
                            .descripcion(carrera.getDescripcion())
                            .areaInteres(nombreArea)
                            .porcentajeCompatibilidad(Math.round(porcentaje * 100.0) / 100.0)
                            .build()
            );
        }

        return GenerateReportResponse.builder()
                .idResultado(resultado.getId())
                .completadoEn(resultado.getCompletadoEn())
                .puntajes(puntajesPorArea) // esto es lo que el front puede graficar
                .topCarreras(topCarreras)
                .build();
    }

    /**
     * Por ahora devolvemos un PDF falso (bytes) para que el controller no falle.
     * Luego aquí ya metes iText/OpenPDF.
     */
    public byte[] generarPdfResultado(Long idResultado, UUID idUsuario) {
        // puedes reutilizar el método anterior
        GenerateReportResponse data = obtenerResultadoConRecomendaciones(idResultado, idUsuario);
        String contenido = "Resultado #" + data.getIdResultado() + " - generado desde el servicio.\n";
        return contenido.getBytes(StandardCharsets.UTF_8);
    }

    // =========================
    // helpers
    // =========================
    private Map<String, Integer> parsearPuntajes(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Integer>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
