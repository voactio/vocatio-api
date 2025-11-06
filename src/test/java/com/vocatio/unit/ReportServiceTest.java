package com.vocatio.unit;

import com.vocatio.dto.response.CarreraAfinDto;
import com.vocatio.dto.response.GenerateReportResponse;
import com.vocatio.exception.ResourceNorFoundException;
import com.vocatio.model.*;
import com.vocatio.repository.*;
import com.vocatio.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    // ====== mocks de repos ======
    private ResultadosTestRepository resultadosTestRepository;
    private AreaInteresRepository areaInteresRepository;
    private CarreraAreaInteresRepository carreraAreaInteresRepository;
    private CarreraRepository carreraRepository;

    // ====== servicio a probar ======
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        resultadosTestRepository = mock(ResultadosTestRepository.class);
        areaInteresRepository = mock(AreaInteresRepository.class);
        carreraAreaInteresRepository = mock(CarreraAreaInteresRepository.class);
        carreraRepository = mock(CarreraRepository.class);

        reportService = new ReportService(
                resultadosTestRepository,
                areaInteresRepository,
                carreraAreaInteresRepository,
                carreraRepository
        );
    }

    // TEST 1: se obtiene el resultado de los test
    @Test
    void obtenerResultadoConRecomendaciones_ok() {
        Long idResultado = 3L;
        UUID idUsuario = UUID.fromString("5e58a502-77c2-4f75-9a6c-cfd9c3afbd8a");

        ResultadosTest rt = new ResultadosTest();
        rt.setId(idResultado);
        rt.setIdUsuario(idUsuario);
        rt.setCompletadoEn(LocalDateTime.now());
        rt.setPuntajes("""
                {
                  "Ciberseguridad": 9,
                  "Sistemas y redes": 7,
                  "Programación y desarrollo de software": 4
                }
                """);

        when(resultadosTestRepository.findByIdAndIdUsuario(idResultado, idUsuario))
                .thenReturn(Optional.of(rt));

        AreaInteres a1 = new AreaInteres();
        a1.setId(1L);
        a1.setNombre("Ciberseguridad");

        AreaInteres a2 = new AreaInteres();
        a2.setId(2L);
        a2.setNombre("Sistemas y redes");

        AreaInteres a3 = new AreaInteres();
        a3.setId(3L);
        a3.setNombre("Programación y desarrollo de software");

        when(areaInteresRepository.findAll())
                .thenReturn(List.of(a1, a2, a3));

        CarreraAreaInteres rel1 = new CarreraAreaInteres();
        rel1.setIdCarrera(10L);
        rel1.setIdAreaInteres(1L);
        rel1.setPuntajeRelevancia(100);

        CarreraAreaInteres rel2 = new CarreraAreaInteres();
        rel2.setIdCarrera(10L);
        rel2.setIdAreaInteres(2L);
        rel2.setPuntajeRelevancia(70);

        when(carreraAreaInteresRepository.findByIdAreaInteresIn(Set.of(1L, 2L, 3L)))
                .thenReturn(List.of(rel1, rel2));

        Carrera car = new Carrera();
        setField(car, "id", 10L);
        car.setNombre("Ingeniería en Ciberseguridad");
        car.setDescripcion("Protección de sistemas");
        car.setDuracionAnios(5);

        when(carreraRepository.findAllById(List.of(10L)))
                .thenReturn(List.of(car));

        GenerateReportResponse resp =
                reportService.obtenerResultadoConRecomendaciones(idResultado, idUsuario);

        assertNotNull(resp);
        assertEquals(idResultado, resp.getIdResultado());
        assertEquals(3, resp.getPuntajes().size());
        assertFalse(resp.getTopCarreras().isEmpty());

        CarreraAfinDto primera = resp.getTopCarreras().get(0);
        assertEquals("Ingeniería en Ciberseguridad", primera.getNombre());

        verify(resultadosTestRepository, times(1))
                .findByIdAndIdUsuario(idResultado, idUsuario);
        verify(areaInteresRepository, times(1)).findAll();
    }

    // TEST 2: resultado no existe o no es del usuario
    @Test
    void obtenerResultadoConRecomendaciones_noExiste_lanza404() {
        Long idResultado = 999L;
        UUID idUsuario = UUID.randomUUID();

        when(resultadosTestRepository.findByIdAndIdUsuario(anyLong(), any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNorFoundException.class,
                () -> reportService.obtenerResultadoConRecomendaciones(idResultado, idUsuario));
    }

    // TEST 3: el resultado existe pero el JSON de puntajes viene vacío
    @Test
    void obtenerResultadoConRecomendaciones_jsonVacio_devuelveVacio() {
        Long idResultado = 5L;
        UUID idUsuario = UUID.randomUUID();

        ResultadosTest rt = new ResultadosTest();
        rt.setId(idResultado);
        rt.setIdUsuario(idUsuario);
        rt.setCompletadoEn(LocalDateTime.now());
        rt.setPuntajes("{}");

        when(resultadosTestRepository.findByIdAndIdUsuario(idResultado, idUsuario))
                .thenReturn(Optional.of(rt));

        when(areaInteresRepository.findAll()).thenReturn(List.of());

        GenerateReportResponse resp =
                reportService.obtenerResultadoConRecomendaciones(idResultado, idUsuario);

        assertNotNull(resp);
        assertTrue(resp.getPuntajes().isEmpty());
        assertTrue(resp.getTopCarreras().isEmpty());
    }
    // TEST 4: generar PDF
    @Test
    void generarPdfResultado_devuelveBytes() {
        Long idResultado = 3L;
        UUID idUsuario = UUID.fromString("5e58a502-77c2-4f75-9a6c-cfd9c3afbd8a");

        ResultadosTest rt = new ResultadosTest();
        rt.setId(idResultado);
        rt.setIdUsuario(idUsuario);
        rt.setCompletadoEn(LocalDateTime.now());
        rt.setPuntajes("""
            { "Ciberseguridad": 9 }
            """);
        when(resultadosTestRepository.findByIdAndIdUsuario(idResultado, idUsuario))
                .thenReturn(Optional.of(rt));
        when(areaInteresRepository.findAll()).thenReturn(List.of());
        when(carreraAreaInteresRepository.findByIdAreaInteresIn(anySet())).thenReturn(List.of());
        when(carreraRepository.findAllById(anyList())).thenReturn(List.of());

        byte[] pdfFake = reportService.generarPdfResultado(idResultado, idUsuario);

        assertNotNull(pdfFake);
        assertTrue(pdfFake.length > 0);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
