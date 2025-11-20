package com.vocatio.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocatio.dto.request.SubmitAnswerRequestDTO;
import com.vocatio.dto.response.PreguntaDTO;
import com.vocatio.dto.response.ResultadoTestDTO;
import com.vocatio.dto.response.StartTestResponseDTO;
import com.vocatio.exception.BadRequestException;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.*;
import com.vocatio.repository.*;
import com.vocatio.service.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestService - Pruebas Unitarias (US11 y US12)")
class TestServiceTest {

    @Mock private TestRepository testRepository;
    @Mock private PreguntaRepository preguntaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private TestSessionRepository testSessionRepository;
    @Mock private OpcionRepository opcionRepository;
    @Mock private CarreraRepository carreraRepository;
    @Mock private ResultadosTestRepository resultadosTestRepository;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private TestService testService;

    private Usuario usuario;
    private com.vocatio.model.Test test;
    private UUID userId;
    private Long testId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        usuario = new Usuario();
        usuario.setId(userId);
        usuario.setNombre("Estudiante Test");
        usuario.setCorreo("estudiante@test.com");

        testId = 1L;
        test = new com.vocatio.model.Test();
        test.setId(testId);
        test.setTitulo("Test Vocacional");
    }

    // US11: INICIAR TEST Y RESPONDER

    // TEST 1: Iniciar Test - Éxito
    @Test
    @DisplayName("US11 - T1: Iniciar Test crea sesión y devuelve primera pregunta")
    void iniciarTest_Exito() {
        // Arrange
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        TestSession session = new TestSession();
        session.setId(100L);
        when(testSessionRepository.save(any(TestSession.class))).thenReturn(session);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(1L);
        pregunta.setTextoPregunta("¿Pregunta 1?");
        pregunta.setOrden(1);
        pregunta.setTest(test);
        pregunta.setOpciones(new ArrayList<>());

        test.setPreguntas(List.of(pregunta));

        when(preguntaRepository.findByTestIdAndOrden(testId, 1)).thenReturn(Optional.of(pregunta));

        StartTestResponseDTO response = testService.iniciarTest(testId, userId);

        assertNotNull(response);
        assertEquals(100L, response.getSessionId());
        assertEquals("¿Pregunta 1?", response.getPrimeraPregunta().getTextoPregunta());
        verify(testSessionRepository).save(any(TestSession.class));
    }

    // TEST 2: Responder Pregunta - Éxito
    @Test
    @DisplayName("US11 - T2: Responder pregunta válida devuelve la siguiente pregunta")
    void submitAnswer_SiguientePregunta() {
        // Arrange
        Long sessionId = 100L;
        SubmitAnswerRequestDTO request = new SubmitAnswerRequestDTO();
        request.setPreguntaId(1L);
        request.setOpcionId(10L);

        TestSession session = new TestSession();
        session.setId(sessionId);
        session.setEstado("IN_PROGRESS");
        session.setTest(test);
        session.setRespuestas(new HashMap<>());

        Opcion opcionMock = new Opcion();
        opcionMock.setId(10L);
        Pregunta pAnterior = new Pregunta();
        pAnterior.setId(1L);
        opcionMock.setPregunta(pAnterior);

        when(testSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(opcionRepository.findById(10L)).thenReturn(Optional.of(opcionMock));

        Pregunta pregunta2 = new Pregunta();
        pregunta2.setId(2L);
        pregunta2.setOrden(2);
        pregunta2.setTextoPregunta("Pregunta 2");
        pregunta2.setTest(test);
        pregunta2.setOpciones(new ArrayList<>());
        test.setPreguntas(List.of(pAnterior, pregunta2));

        when(preguntaRepository.findByTestIdAndOrden(testId, 2)).thenReturn(Optional.of(pregunta2));

        PreguntaDTO result = testService.submitAnswerAndGetNext(sessionId, request);

        assertNotNull(result);
        assertEquals("Pregunta 2", result.getTextoPregunta());
    }

    // TEST 3: Responder Pregunta - Fin del Test
    @Test
    @DisplayName("US11 - T3: Responder última pregunta finaliza el test y guarda resultados")
    void submitAnswer_FinalizarTest() throws JsonProcessingException {
        // Arrange
        Long sessionId = 100L;
        SubmitAnswerRequestDTO request = new SubmitAnswerRequestDTO();
        request.setPreguntaId(5L);
        request.setOpcionId(50L);

        TestSession session = new TestSession();
        session.setId(sessionId);
        session.setEstado("IN_PROGRESS");
        session.setUsuario(usuario);
        session.setTest(test);
        session.setRespuestas(new HashMap<>());

        Opcion opcionMock = new Opcion();
        opcionMock.setId(50L);
        Pregunta pUltima = new Pregunta();
        pUltima.setId(5L);
        opcionMock.setPregunta(pUltima);

        AreaInteres area = new AreaInteres();
        area.setNombre("Realista");
        opcionMock.setAreaInteres(area);

        when(testSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(opcionRepository.findById(50L)).thenReturn(Optional.of(opcionMock));

        when(preguntaRepository.findByTestIdAndOrden(testId, 2)).thenReturn(Optional.empty());

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"Realista\": 1}");

        PreguntaDTO result = testService.submitAnswerAndGetNext(sessionId, request);

        assertNull(result);
        assertEquals("COMPLETED", session.getEstado());
        verify(testSessionRepository).save(session);
        verify(resultadosTestRepository).save(any(ResultadosTest.class));
    }

    // TEST 4: Responder con Opción que no pertenece a la Pregunta
    @Test
    @DisplayName("US11 - T4: Opción inválida (no corresponde a la pregunta) lanza BadRequestException")
    void submitAnswer_OpcionNoCorresponde() {
        // Arrange
        Long sessionId = 100L;
        SubmitAnswerRequestDTO request = new SubmitAnswerRequestDTO();
        request.setPreguntaId(1L);
        request.setOpcionId(99L);

        TestSession session = new TestSession();
        session.setEstado("IN_PROGRESS");
        when(testSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        Opcion opcionTrampa = new Opcion();
        opcionTrampa.setId(99L);
        Pregunta otraPregunta = new Pregunta();
        otraPregunta.setId(2L);
        opcionTrampa.setPregunta(otraPregunta);

        when(opcionRepository.findById(99L)).thenReturn(Optional.of(opcionTrampa));

        // Act & Assert
        assertThrows(BadRequestException.class, () ->
                testService.submitAnswerAndGetNext(sessionId, request)
        );
    }


    // US12: VER RESULTADOS DEL TEST

    // TEST 5: Obtener Resultados - Éxito
    @Test
    @DisplayName("US12 - T1: Obtener resultados calcula el ranking correctamente")
    void getTestResults_Exito() {
        Long sessionId = 200L;
        TestSession session = new TestSession();
        session.setEstado("COMPLETED");
        session.setUsuario(usuario);
        session.setRespuestas(new HashMap<>());
        session.getRespuestas().put(1L, 10L);

        when(testSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        Opcion opcionElegida = new Opcion();
        AreaInteres area = new AreaInteres();
        area.setNombre("Investigador");
        opcionElegida.setAreaInteres(area);

        when(opcionRepository.findById(10L)).thenReturn(Optional.of(opcionElegida));

        Carrera carrera1 = new Carrera();
        carrera1.setId(1L);
        carrera1.setNombre("Ing. Sistemas");
        carrera1.setPerfilRiasec("Investigador");

        Carrera carrera2 = new Carrera();
        carrera2.setId(2L);
        carrera2.setNombre("Arte");
        carrera2.setPerfilRiasec("Artístico");

        when(carreraRepository.findAll()).thenReturn(List.of(carrera1, carrera2));

        ResultadoTestDTO result = testService.getTestResults(sessionId);

        assertNotNull(result);
        assertNotNull(result.getGraficoIntereses());
        assertTrue(result.getGraficoIntereses().getPuntajes().containsKey("Investigador"));

        assertFalse(result.getRankingCarreras().isEmpty());
        assertEquals("Ing. Sistemas", result.getRankingCarreras().get(0).getNombre());
    }

    // TEST 6: Test No Completado
    @Test
    @DisplayName("US12 - T2: Intentar ver resultados de test IN_PROGRESS lanza BadRequest")
    void getTestResults_NoCompletado() {
        Long sessionId = 200L;
        TestSession session = new TestSession();
        session.setEstado("IN_PROGRESS");

        when(testSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> testService.getTestResults(sessionId));
    }

    // TEST 7: Sesión No Encontrada
    @Test
    @DisplayName("US12 - T3: Sesión inexistente lanza ResourceNotFoundException")
    void getTestResults_SesionNoExiste() {
        when(testSessionRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> testService.getTestResults(999L));
    }

    // TEST 8: Cálculo de Compatibilidad
    @Test
    @DisplayName("US12 - T4: Verifica que carreras sin coincidencia tengan score bajo")
    void getTestResults_SinCoincidencia() {
        // Arrange
        Long sessionId = 200L;
        TestSession session = new TestSession();
        session.setEstado("COMPLETED");
        session.setRespuestas(new HashMap<>());
        session.getRespuestas().put(1L, 10L);

        when(testSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        Opcion op = new Opcion();
        AreaInteres area = new AreaInteres();
        area.setNombre("Artístico");
        op.setAreaInteres(area);
        when(opcionRepository.findById(10L)).thenReturn(Optional.of(op));

        Carrera carrera = new Carrera();
        carrera.setNombre("Mecánica");
        carrera.setPerfilRiasec("Realista");

        when(carreraRepository.findAll()).thenReturn(List.of(carrera));

        ResultadoTestDTO result = testService.getTestResults(sessionId);

        double compatibilidad = result.getRankingCarreras().get(0).getPorcentajeCompatibilidad();
        assertTrue(compatibilidad < 60.0, "La compatibilidad debería ser baja para perfiles opuestos");
    }
}