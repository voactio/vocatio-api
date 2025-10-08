package com.vocatio.service;

import com.vocatio.dto.response.CarreraRankingDTO;
import com.vocatio.dto.response.GraficoInteresDTO;
import com.vocatio.dto.response.OpcionDTO;
import com.vocatio.dto.response.PreguntaDTO;
import com.vocatio.dto.response.ResultadoTestDTO;
import com.vocatio.dto.response.StartTestResponseDTO;
import com.vocatio.model.Carrera;
import com.vocatio.model.Pregunta;
import com.vocatio.model.Test;
import com.vocatio.model.TestSession;
import com.vocatio.model.Usuario;
import com.vocatio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TestService {

    private final TestRepository testRepository;
    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TestSessionRepository testSessionRepository;
    private final OpcionRepository opcionRepository;
    private final CarreraRepository carreraRepository;

    public TestService(TestRepository testRepository, PreguntaRepository preguntaRepository, UsuarioRepository usuarioRepository, TestSessionRepository testSessionRepository, OpcionRepository opcionRepository, CarreraRepository carreraRepository) {
        this.testRepository = testRepository;
        this.preguntaRepository = preguntaRepository;
        this.usuarioRepository = usuarioRepository;
        this.testSessionRepository = testSessionRepository;
        this.opcionRepository = opcionRepository;
        this.carreraRepository = carreraRepository;
    }

    @Transactional
    public StartTestResponseDTO iniciarTest(Long testId, Long userId) {
        Usuario usuario = usuarioRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Test test = testRepository.findById(testId).orElseThrow(() -> new RuntimeException("Test no encontrado"));

        TestSession newSession = new TestSession();
        newSession.setUsuario(usuario);
        newSession.setTest(test);
        newSession.setEstado("IN_PROGRESS");
        TestSession savedSession = testSessionRepository.save(newSession);

        Pregunta primeraPregunta = preguntaRepository.findByTestIdAndOrden(testId, 1)
                .orElseThrow(() -> new RuntimeException("No se encontró la primera pregunta para el test"));

        StartTestResponseDTO response = new StartTestResponseDTO();
        response.setSessionId(savedSession.getId());
        response.setPrimeraPregunta(convertirAPreguntaDTO(primeraPregunta));
        return response;
    }

    @Transactional
    public PreguntaDTO submitAnswerAndGetNext(Long sessionId, Long preguntaId, Long opcionId) {
        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión de test no encontrada"));

        if (!"IN_PROGRESS".equals(session.getEstado())) {
            throw new RuntimeException("Este test ya ha sido completado.");
        }

        var opcionSeleccionada = opcionRepository.findById(opcionId)
                .orElseThrow(() -> new IllegalArgumentException("La opción con id " + opcionId + " no es válida."));

        if (!opcionSeleccionada.getPregunta().getId().equals(preguntaId)) {
            throw new IllegalArgumentException("La opción seleccionada no pertenece a la pregunta actual.");
        }

        session.getRespuestas().put(preguntaId, opcionId);

        int currentOrder = session.getRespuestas().size();
        int nextOrder = currentOrder + 1;

        var nextQuestionOpt = preguntaRepository.findByTestIdAndOrden(session.getTest().getId(), nextOrder);

        if (nextQuestionOpt.isPresent()) {
            testSessionRepository.save(session);
            return convertirAPreguntaDTO(nextQuestionOpt.get());
        } else {
            session.setEstado("COMPLETED");
            session.setCompletadoEn(LocalDateTime.now());
            testSessionRepository.save(session);
            return null;
        }
    }

    public ResultadoTestDTO getTestResults(Long sessionId) {
        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión de test no encontrada"));

        if (!"COMPLETED".equals(session.getEstado())) {
            throw new RuntimeException("El test para esta sesión aún no ha sido completado.");
        }

        // 1. Calcular el perfil RIASEC del usuario contando las áreas de interés de sus respuestas
        Map<String, Long> conteoAreas = session.getRespuestas().values().stream()
                .map(opcionId -> opcionRepository.findById(opcionId).orElseThrow())
                .map(opcion -> opcion.getAreaInteres().getId())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 2. Normalizar los puntajes para el gráfico (0-100)
        long maxPuntaje = conteoAreas.values().stream().max(Long::compareTo).orElse(1L);
        Map<String, Integer> puntajesNormalizados = new HashMap<>();
        conteoAreas.forEach((area, puntaje) -> {
            puntajesNormalizados.put(area, (int) ((puntaje * 100) / maxPuntaje));
        });

        GraficoInteresDTO graficoDTO = new GraficoInteresDTO();
        graficoDTO.setPuntajes(puntajesNormalizados);

        // 3. Obtener el código de 3 letras del perfil del usuario (ej. "IEC")
        String perfilUsuario = conteoAreas.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining());

        // 4. Calcular compatibilidad y generar el ranking de carreras
        List<Carrera> todasLasCarreras = carreraRepository.findAll();
        List<CarreraRankingDTO> ranking = todasLasCarreras.stream().map(carrera -> {
                    int compatibilidad = calcularCompatibilidad(perfilUsuario, carrera.getPerfilRiasec());

                    CarreraRankingDTO dto = new CarreraRankingDTO();
                    dto.setId(carrera.getId());
                    dto.setNombre(carrera.getNombre());
                    dto.setDescripcionCorta(carrera.getDescripcion());
                    dto.setCompatibilidad(compatibilidad);
                    dto.setTagPrincipal(carrera.getPerfilRiasec()); // Usamos el perfil como tag principal
                    return dto;
                })
                .sorted(Comparator.comparingInt(CarreraRankingDTO::getCompatibilidad).reversed())
                .limit(5) // Limitar al Top 5
                .collect(Collectors.toList());

        // 5. Ensamblar el DTO de resultado final
        ResultadoTestDTO resultadoFinal = new ResultadoTestDTO();
        resultadoFinal.setGraficoIntereses(graficoDTO);
        resultadoFinal.setRankingCarreras(ranking);

        return resultadoFinal;
    }

    private int calcularCompatibilidad(String perfilUsuario, String perfilCarrera) {
        if (perfilCarrera == null || perfilCarrera.isEmpty()) return 0;
        int score = 0;
        for (int i = 0; i < perfilUsuario.length(); i++) {
            char letraUsuario = perfilUsuario.charAt(i);
            int indexEnCarrera = perfilCarrera.indexOf(letraUsuario);
            if (indexEnCarrera != -1) {
                score += (3 - indexEnCarrera);
            }
        }
        return Math.min(100, (score * 15) + new Random().nextInt(10) + 35);
    }

    private PreguntaDTO convertirAPreguntaDTO(Pregunta pregunta) {
        PreguntaDTO dto = new PreguntaDTO();
        dto.setId(pregunta.getId());
        dto.setTextoPregunta(pregunta.getTextoPregunta());

        int totalPreguntas = pregunta.getTest().getPreguntas().size();
        dto.setProgreso("Pregunta " + pregunta.getOrden() + " de " + totalPreguntas);

        dto.setOpciones(pregunta.getOpciones().stream().map(opcion -> {
            OpcionDTO opcionDTO = new OpcionDTO();
            opcionDTO.setId(opcion.getId());
            opcionDTO.setTextoOpcion(opcion.getTextoOpcion());
            return opcionDTO;
        }).collect(Collectors.toList()));

        return dto;
    }
}