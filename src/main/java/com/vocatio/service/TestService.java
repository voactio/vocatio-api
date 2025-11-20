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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
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

    // --- US11: INICIAR Y RESPONDER ---

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

        Optional<Pregunta> nextQuestionOpt = preguntaRepository.findByTestIdAndOrden(session.getTest().getId(), nextOrder);

        if (nextQuestionOpt.isPresent()) {
            testSessionRepository.save(session);
            return convertirAPreguntaDTO(nextQuestionOpt.get());
        } else {
            finalizarTest(session);
            return null;
        }
    }

    private void finalizarTest(TestSession session) {
        session.setEstado("COMPLETED");
        session.setCompletadoEn(LocalDateTime.now());
        testSessionRepository.save(session);
        guardarResultadosTest(session);
    }

    private void guardarResultadosTest(TestSession session) {
        Map<String, Integer> puntajes = new HashMap<>();
        for (Long opcionId : session.getRespuestas().values()) {
            Opcion op = opcionRepository.findById(opcionId).orElseThrow();
            if (op.getAreaInteres() != null) {
                String nombreArea = op.getAreaInteres().getNombre();
                puntajes.merge(nombreArea, 1, Integer::sum);
            }
        }

        String puntajesJson;
        try {
            puntajesJson = objectMapper.writeValueAsString(puntajes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar resultados a JSON", e);
        }

        ResultadosTest resultado = new ResultadosTest();
        resultado.setIdUsuario(session.getUsuario().getId());
        resultado.setIdTest(session.getTest().getId());
        resultado.setCompletadoEn(OffsetDateTime.now().toLocalDateTime());

        resultado.setPuntajes(puntajesJson);

        resultado.setIntento((int) (System.currentTimeMillis() % 100000));

        resultadosTestRepository.save(resultado);
    }

    // --- US12: OBTENER RESULTADOS ---

    public ResultadoTestDTO getTestResults(Long sessionId) {
        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada"));

        if (!"COMPLETED".equals(session.getEstado())) {
            throw new BadRequestException("El test no ha finalizado.");
        }

        Map<String, Long> conteo = session.getRespuestas().values().stream()
                .map(id -> opcionRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(op -> op.getAreaInteres() != null)
                .map(op -> op.getAreaInteres().getNombre()) // Ej: "Investigador"
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Integer> puntajesDTO = new HashMap<>();
        conteo.forEach((k, v) -> puntajesDTO.put(k, v.intValue()));

        String areaDominante = conteo.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");

        List<Carrera> todas = carreraRepository.findAll();

        List<CarreraAfinDto> ranking = todas.stream()
                .map(c -> {
                    double porcentaje = 0.0;
                    // Lógica de Match: Si el perfil de la carrera contiene el área dominante
                    if (c.getPerfilRiasec() != null && !areaDominante.isEmpty() &&
                            c.getPerfilRiasec().contains(areaDominante)) {
                        porcentaje = 85.0 + new Random().nextInt(15); // 85% - 100%
                    } else {
                        porcentaje = 20.0 + new Random().nextInt(30); // Bajo
                    }

                    return CarreraAfinDto.builder()
                            .id(c.getId())
                            .nombre(c.getNombre())
                            .descripcion(c.getDescripcion() != null && c.getDescripcion().length() > 100
                                    ? c.getDescripcion().substring(0, 100) + "..."
                                    : c.getDescripcion())
                            .areaInteres(c.getPerfilRiasec()) // Mostramos el perfil como área
                            .porcentajeCompatibilidad(Math.round(porcentaje * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparingDouble(CarreraAfinDto::getPorcentajeCompatibilidad).reversed())
                .limit(5)
                .collect(Collectors.toList());

        ResultadoTestDTO response = new ResultadoTestDTO();
        GraficoInteresDTO grafico = new GraficoInteresDTO();
        grafico.setPuntajes(puntajesDTO);
        response.setGraficoIntereses(grafico);
        response.setRankingCarreras(ranking);

        return response;
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