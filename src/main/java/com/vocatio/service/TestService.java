package com.vocatio.service;

import com.vocatio.dto.request.SubmitAnswerRequestDTO;
import com.vocatio.dto.response.OpcionDTO;
import com.vocatio.dto.response.PreguntaDTO;
import com.vocatio.dto.response.StartTestResponseDTO;
import com.vocatio.model.Opcion;
import com.vocatio.model.Pregunta;
import com.vocatio.model.Test;
import com.vocatio.model.TestSession;
import com.vocatio.model.Usuario;
import com.vocatio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class TestService {

    private final TestRepository testRepository;
    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TestSessionRepository testSessionRepository;
    private final OpcionRepository opcionRepository;

    public TestService(TestRepository testRepository, PreguntaRepository preguntaRepository, UsuarioRepository usuarioRepository, TestSessionRepository testSessionRepository, OpcionRepository opcionRepository) {
        this.testRepository = testRepository;
        this.preguntaRepository = preguntaRepository;
        this.usuarioRepository = usuarioRepository;
        this.testSessionRepository = testSessionRepository;
        this.opcionRepository = opcionRepository;
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
    public PreguntaDTO submitAnswerAndGetNext(Long sessionId, SubmitAnswerRequestDTO answerRequest) {
        Long preguntaId = answerRequest.getPreguntaId();
        Long opcionId = answerRequest.getOpcionId();

        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión de test no encontrada"));

        if (!"IN_PROGRESS".equals(session.getEstado())) {
            throw new RuntimeException("Este test ya ha sido completado.");
        }

        Opcion opcionSeleccionada = opcionRepository.findById(opcionId)
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