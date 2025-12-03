package com.vocatio.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocatio.dto.request.SubmitAnswerRequestDTO;
import com.vocatio.dto.response.*;
import com.vocatio.exception.BadRequestException;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.*;
import com.vocatio.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TestSessionRepository testSessionRepository;
    private final OpcionRepository opcionRepository;
    private final CarreraRepository carreraRepository;
    private final ResultadosTestRepository resultadosTestRepository;
    private final ObjectMapper objectMapper;
    private final ResultadoCarreraRepository resultadoCarreraRepository;
    private final AreaInteresRepository areaInteresRepository;
    private final CarreraAreaInteresRepository carreraAreaInteresRepository;

    // --- INICIAR TEST ---
    @Transactional
    public StartTestResponseDTO iniciarTest(Long testId, UUID userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test no encontrado"));

        TestSession newSession = new TestSession();
        newSession.setUsuario(usuario);
        newSession.setTest(test);
        newSession.setEstado("IN_PROGRESS");

        TestSession savedSession = testSessionRepository.save(newSession);

        Pregunta primeraPregunta = preguntaRepository.findByTestIdAndOrden(testId, 1)
                .orElseThrow(() -> new ResourceNotFoundException("El test no tiene preguntas configuradas."));

        StartTestResponseDTO response = new StartTestResponseDTO();
        response.setSessionId(savedSession.getId());
        response.setPrimeraPregunta(convertirAPreguntaDTO(primeraPregunta));
        return response;
    }

    // --- RESPONDER Y AVANZAR ---
    @Transactional
    public PreguntaDTO submitAnswerAndGetNext(Long sessionId, SubmitAnswerRequestDTO answerRequest) {
        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión de test no encontrada"));

        if (!"IN_PROGRESS".equals(session.getEstado())) {
            throw new BadRequestException("Este test ya ha sido completado.");
        }

        Long preguntaId = answerRequest.getPreguntaId();
        Long opcionId = answerRequest.getOpcionId();

        Opcion opcionSeleccionada = opcionRepository.findById(opcionId)
                .orElseThrow(() -> new BadRequestException("Opción inválida"));

        if (!opcionSeleccionada.getPregunta().getId().equals(preguntaId)) {
            throw new BadRequestException("La opción no corresponde a la pregunta enviada.");
        }

        session.getRespuestas().put(preguntaId, opcionId);

        int currentOrder = session.getRespuestas().size();
        int nextOrder = currentOrder + 1;

        Optional<Pregunta> nextQuestionOpt = preguntaRepository.findByTestIdAndOrden(
                session.getTest().getId(), nextOrder
        );

        if (nextQuestionOpt.isPresent()) {
            testSessionRepository.save(session);
            return convertirAPreguntaDTO(nextQuestionOpt.get());
        } else {
            finalizarTest(session);
            return null;
        }
    }

    // --- FINALIZAR TEST ---
    private void finalizarTest(TestSession session) {
        session.setEstado("COMPLETED");
        session.setCompletadoEn(LocalDateTime.now());
        testSessionRepository.save(session);
        guardarResultadosTest(session);
    }

    // --- GUARDAR RESULTADOS ---
    private void guardarResultadosTest(TestSession session) {
        // 1. Calcular puntajes por área (ID)
        Map<Long, Integer> puntajesUsuario = calcularPuntajesUsuario(session);

        // 2. Convertir a Map con nombres de áreas para guardar
        Map<String, Integer> puntajesParaJSON = convertirPuntajesANombres(puntajesUsuario);

        // 3. Guardar resultado general (ahora puntajes es Map directamente, no String)
        ResultadosTest resultado = new ResultadosTest();
        resultado.setIdUsuario(session.getUsuario().getId());
        resultado.setIdTest(session.getTest().getId());
        resultado.setCompletadoEn(OffsetDateTime.now().toLocalDateTime());
        resultado.setPuntajes(puntajesParaJSON);  // Directamente el Map
        resultado.setIntento((int) (System.currentTimeMillis() % 100000));

        ResultadosTest savedResult = resultadosTestRepository.save(resultado);

        // 4. Calcular top carreras con ponderación
        List<Carrera> todasLasCarreras = carreraRepository.findAll();

        List<Carrera> ranking = todasLasCarreras.stream()
                .map(c -> new AbstractMap.SimpleEntry<>(
                        c,
                        calcularCompatibilidadPonderada(puntajesUsuario, c)
                ))
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        // 5. Guardar top 5
        int orden = 1;
        for (Carrera carrera : ranking) {
            double porcentaje = calcularCompatibilidadPonderada(puntajesUsuario, carrera);

            ResultadoCarrera rc = new ResultadoCarrera();
            rc.setResultadoTest(savedResult);
            rc.setCarrera(carrera);
            rc.setPorcentaje(porcentaje);
            rc.setOrden(orden++);

            resultadoCarreraRepository.save(rc);
        }
    }

    // --- OBTENER RESULTADOS ---
    public ResultadoTestDTO getTestResults(Long sessionId) {
        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada"));

        if (!"COMPLETED".equals(session.getEstado())) {
            throw new BadRequestException("El test no ha finalizado.");
        }

        // 1. Calcular puntajes del usuario
        Map<Long, Integer> puntajesUsuario = calcularPuntajesUsuario(session);
        Map<String, Integer> puntajesDTO = convertirPuntajesANombres(puntajesUsuario);

        // 2. Calcular ranking con ponderación
        List<Carrera> todasLasCarreras = carreraRepository.findAll();

        List<CarreraAfinDto> ranking = todasLasCarreras.stream()
                .map(carrera -> {
                    double compatibilidad = calcularCompatibilidadPonderada(puntajesUsuario, carrera);

                    return CarreraAfinDto.builder()
                            .id(carrera.getId())
                            .nombre(carrera.getNombre())
                            .descripcion(truncarDescripcion(carrera.getDescripcion()))
                            .areaInteres(obtenerAreaPrincipal(carrera))
                            .porcentajeCompatibilidad(compatibilidad)
                            .build();
                })
                .sorted(Comparator.comparingDouble(
                        CarreraAfinDto::getPorcentajeCompatibilidad
                ).reversed())
                .limit(10)
                .collect(Collectors.toList());

        // 3. Construir respuesta
        ResultadoTestDTO response = new ResultadoTestDTO();
        GraficoInteresDTO grafico = new GraficoInteresDTO();
        grafico.setPuntajes(puntajesDTO);
        response.setGraficoIntereses(grafico);
        response.setRankingCarreras(ranking);

        return response;
    }

    // --- HISTORIAL ---
    public List<TestHistoryResponse> getHistorial(UUID userId) {
        List<ResultadosTest> resultados = resultadosTestRepository
                .findByIdUsuarioOrderByCompletadoEnDesc(userId);

        return resultados.stream().map(res -> {
            List<ResultadoCarrera> carrerasGuardadas =
                    resultadoCarreraRepository.findByResultadoTestOrderByOrdenAsc(res);

            List<CarreraAfinDto> topCarreras = carrerasGuardadas.stream().map(rc ->
                    CarreraAfinDto.builder()
                            .id(rc.getCarrera().getId())
                            .nombre(rc.getCarrera().getNombre())
                            .porcentajeCompatibilidad(rc.getPorcentaje())
                            .areaInteres(obtenerAreaPrincipal(rc.getCarrera()))
                            .build()
            ).toList();

            return TestHistoryResponse.builder()
                    .idResultado(res.getId())
                    .fecha(res.getCompletadoEn())
                    .intento(res.getIntento())
                    .topCarreras(topCarreras)
                    .build();
        }).toList();
    }

    // --- OBTENER RESULTADOS POR ID ---
    public ResultadoTestDTO getResultadosPorId(Long resultadoId, UUID userId) {
        // 1. Buscar el resultado guardado
        ResultadosTest resultado = resultadosTestRepository.findById(resultadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado no encontrado"));

        // 2. Verificar que pertenece al usuario
        if (!resultado.getIdUsuario().equals(userId)) {
            throw new BadRequestException("No tienes permiso para ver este resultado");
        }

        // 3. Obtener los puntajes (ya es un Map, no necesita parseo)
        Map<String, Integer> puntajesDTO = resultado.getPuntajes();

        // 4. Obtener las carreras guardadas para este resultado
        List<ResultadoCarrera> carrerasGuardadas =
                resultadoCarreraRepository.findByResultadoTestOrderByOrdenAsc(resultado);

        List<CarreraAfinDto> ranking = carrerasGuardadas.stream().map(rc ->
                CarreraAfinDto.builder()
                        .id(rc.getCarrera().getId())
                        .nombre(rc.getCarrera().getNombre())
                        .descripcion(truncarDescripcion(rc.getCarrera().getDescripcion()))
                        .areaInteres(obtenerAreaPrincipal(rc.getCarrera()))
                        .porcentajeCompatibilidad(rc.getPorcentaje())
                        .build()
        ).collect(Collectors.toList());

        // 5. Construir respuesta
        ResultadoTestDTO response = new ResultadoTestDTO();
        GraficoInteresDTO grafico = new GraficoInteresDTO();
        grafico.setPuntajes(puntajesDTO);
        response.setGraficoIntereses(grafico);
        response.setRankingCarreras(ranking);

        return response;
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Calcula puntajes del usuario por ID de área
     */
    private Map<Long, Integer> calcularPuntajesUsuario(TestSession session) {
        Map<Long, Integer> puntajes = new HashMap<>();

        for (Long opcionId : session.getRespuestas().values()) {
            Opcion opcion = opcionRepository.findById(opcionId).orElse(null);
            if (opcion != null && opcion.getAreaInteres() != null) {
                Long areaId = opcion.getAreaInteres().getId();
                puntajes.merge(areaId, 1, Integer::sum);
            }
        }

        return puntajes;
    }

    /**
     * Convierte IDs de áreas a nombres
     */
    private Map<String, Integer> convertirPuntajesANombres(Map<Long, Integer> puntajesUsuario) {
        Map<String, Integer> resultado = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : puntajesUsuario.entrySet()) {
            AreaInteres area = areaInteresRepository.findById(entry.getKey()).orElse(null);
            if (area != null) {
                resultado.put(area.getNombre(), entry.getValue());
            }
        }

        return resultado;
    }

    /**
     * ¡ALGORITMO CLAVE! Calcula compatibilidad ponderada
     */
    private double calcularCompatibilidadPonderada(
            Map<Long, Integer> puntajesUsuario,
            Carrera carrera
    ) {
        List<CarreraAreaInteres> areasCarrera =
                carreraAreaInteresRepository.findByIdCarrera(carrera.getId());

        if (areasCarrera.isEmpty()) {
            return 0.0;
        }

        double puntajePonderado = 0.0;
        double sumaPesos = 0.0;

        for (CarreraAreaInteres areaCarrera : areasCarrera) {
            Long areaId = areaCarrera.getAreaInteres().getId();
            int puntosUsuario = puntajesUsuario.getOrDefault(areaId, 0);
            double peso = areaCarrera.getPuntajeRelevancia();

            puntajePonderado += puntosUsuario * peso;
            sumaPesos += peso;
        }

        if (sumaPesos == 0) {
            return 0.0;
        }

        int totalPuntosUsuario = puntajesUsuario.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totalPuntosUsuario == 0) {
            return 0.0;
        }

        // Normalizar: (puntaje ponderado / puntaje máximo teórico) × 100
        double pesoMaximo = areasCarrera.stream()
                .mapToDouble(CarreraAreaInteres::getPuntajeRelevancia)
                .max()
                .orElse(1.0);

        double puntajeMaxTeorico = totalPuntosUsuario * pesoMaximo;

        return Math.min(100.0, (puntajePonderado / puntajeMaxTeorico) * 100);
    }

    /**
     * Obtiene el área principal (mayor peso) de una carrera
     */
    private String obtenerAreaPrincipal(Carrera carrera) {
        return carreraAreaInteresRepository.findByCarreraIdOrderByPuntajeRelevanciaDesc(carrera.getId())
                .stream()
                .findFirst()
                .map(ca -> ca.getAreaInteres().getNombre())
                .orElse("SIN_AREA");
    }

    private String truncarDescripcion(String descripcion) {
        if (descripcion == null) return "";
        return descripcion.length() > 100
                ? descripcion.substring(0, 100) + "..."
                : descripcion;
    }

    private PreguntaDTO convertirAPreguntaDTO(Pregunta p) {
        PreguntaDTO dto = new PreguntaDTO();
        dto.setId(p.getId());
        dto.setTextoPregunta(p.getTextoPregunta());
        int total = p.getTest().getPreguntas().size();
        dto.setProgreso("Pregunta " + p.getOrden() + " de " + total);

        List<OpcionDTO> opcs = p.getOpciones().stream().map(o -> {
            OpcionDTO od = new OpcionDTO();
            od.setId(o.getId());
            od.setTextoOpcion(o.getTextoOpcion());
            return od;
        }).collect(Collectors.toList());
        dto.setOpciones(opcs);
        return dto;
    }
}