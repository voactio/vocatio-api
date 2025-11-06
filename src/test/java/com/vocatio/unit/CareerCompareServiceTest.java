package com.vocatio.unit;

import com.vocatio.dto.request.CompareCareersRequest;
import com.vocatio.dto.response.CompareCareersResponse;
import com.vocatio.exception.BadRequestException;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.ResultadosTestRepository;
import com.vocatio.service.CareerCompareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CareerCompareServiceTest {

    private ResultadosTestRepository resultadosTestRepository;
    private CarreraRepository carreraRepository;
    private CareerCompareService careerCompareService;

    @BeforeEach
    void setUp() {
        resultadosTestRepository = mock(ResultadosTestRepository.class);
        carreraRepository = mock(CarreraRepository.class);

        careerCompareService = new CareerCompareService(
                resultadosTestRepository,
                carreraRepository
        );
    }

    // TEST 1: compara OK
    @Test
    void comparar_ok() {
        UUID idUsuario = UUID.randomUUID();
        Long idResultado = 4L;

        // el top5 que devuelve tu query nativa
        when(resultadosTestRepository.findTop5CarrerasByResultadoAndUsuario(idResultado, idUsuario))
                .thenReturn(List.of(1L, 4L, 7L, 9L, 10L));

        Carrera c1 = new Carrera();
        setId(c1, 1L);
        c1.setNombre("Ingeniería de Sistemas");
        c1.setDescripcion("Desc 1");
        c1.setDuracionAnios(5);
        c1.setModalidad("Presencial");
        c1.setRangoSalarioPromedio("S/ 2500 - S/ 6000");
        when(carreraRepository.findById(1L)).thenReturn(Optional.of(c1));

        Carrera c2 = new Carrera();
        setId(c2, 4L);
        c2.setNombre("Ingeniería en Computación");
        c2.setDescripcion("Desc 2");
        c2.setDuracionAnios(5);
        c2.setModalidad("Presencial");
        c2.setRangoSalarioPromedio("S/ 2400 - S/ 5800");
        when(carreraRepository.findById(4L)).thenReturn(Optional.of(c2));

        CompareCareersRequest req = new CompareCareersRequest();
        req.setIdUsuario(idUsuario);
        req.setIdResultado(idResultado);
        req.setIdCarrera1(1L);
        req.setIdCarrera2(4L);

        CompareCareersResponse resp = careerCompareService.comparar(req);

        assertNotNull(resp);
        assertEquals(1L, resp.getCarrera1().getId());
        assertEquals(4L, resp.getCarrera2().getId());
        assertEquals("Ingeniería de Sistemas", resp.getCarrera1().getNombre());
        assertEquals("Ingeniería en Computación", resp.getCarrera2().getNombre());

        verify(resultadosTestRepository, times(1))
                .findTop5CarrerasByResultadoAndUsuario(idResultado, idUsuario);
        verify(carreraRepository, times(1)).findById(1L);
        verify(carreraRepository, times(1)).findById(4L);
    }

    // TEST 2: no hay ranking
    @Test
    void comparar_sinRanking_lanzaBadRequest() {
        UUID idUsuario = UUID.randomUUID();
        Long idResultado = 4L;

        when(resultadosTestRepository.findTop5CarrerasByResultadoAndUsuario(idResultado, idUsuario))
                .thenReturn(List.of());

        CompareCareersRequest req = new CompareCareersRequest();
        req.setIdUsuario(idUsuario);
        req.setIdResultado(idResultado);
        req.setIdCarrera1(1L);
        req.setIdCarrera2(4L);

        assertThrows(BadRequestException.class,
                () -> careerCompareService.comparar(req));
    }

    // TEST 3: una carrera no está en el top5
    @Test
    void comparar_carreraFueraDelTop_lanzaBadRequest() {
        UUID idUsuario = UUID.randomUUID();
        Long idResultado = 4L;

        when(resultadosTestRepository.findTop5CarrerasByResultadoAndUsuario(idResultado, idUsuario))
                .thenReturn(List.of(1L, 4L, 7L, 9L, 10L));

        CompareCareersRequest req = new CompareCareersRequest();
        req.setIdUsuario(idUsuario);
        req.setIdResultado(idResultado);
        req.setIdCarrera1(1L);
        req.setIdCarrera2(99L);

        assertThrows(BadRequestException.class,
                () -> careerCompareService.comparar(req));
    }

    // TEST 4: una carrera del top no existe en BD
    @Test
    void comparar_carreraNoExiste_lanza404() {
        UUID idUsuario = UUID.randomUUID();
        Long idResultado = 4L;

        when(resultadosTestRepository.findTop5CarrerasByResultadoAndUsuario(idResultado, idUsuario))
                .thenReturn(List.of(1L, 4L, 7L, 9L, 10L));

        // carrera 1 sí
        Carrera c1 = new Carrera();
        setId(c1, 1L);
        c1.setNombre("Ingeniería de Sistemas");
        when(carreraRepository.findById(1L)).thenReturn(Optional.of(c1));

        // carrera 4 NO
        when(carreraRepository.findById(4L)).thenReturn(Optional.empty());

        CompareCareersRequest req = new CompareCareersRequest();
        req.setIdUsuario(idUsuario);
        req.setIdResultado(idResultado);
        req.setIdCarrera1(1L);
        req.setIdCarrera2(4L);

        assertThrows(ResourceNotFoundException.class,
                () -> careerCompareService.comparar(req));
    }

    private static void setId(Object target, Long value) {
        try {
            Field f = target.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
