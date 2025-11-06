package com.vocatio.unit;

import com.vocatio.dto.response.TestimonioResponse;
import com.vocatio.model.Carrera;
import com.vocatio.model.Testimonio;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.TestimonioRepository;
import com.vocatio.service.TestimonioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestimonioService - Unit")
class TestimonioServiceTest {

    @Mock
    private TestimonioRepository testimonioRepository;

    @Mock
    private CarreraRepository carreraRepository;

    @InjectMocks
    private TestimonioService testimonioService;

    @Test
    @DisplayName("obtenerAprobadosPorCarrera lanza 404 si carrera no existe")
    void obtenerAprobados_404() {
        Long carreraId = 999L;
        when(carreraRepository.findById(carreraId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> testimonioService.obtenerAprobadosPorCarrera(carreraId));
    }

    @Test
    @DisplayName("obtenerAprobadosPorCarrera devuelve aprobados mapeados y ordenados")
    void obtenerAprobados_ok() {
        Long carreraId = 1L;
        Carrera carrera = new Carrera();
        try {
            var fId = Carrera.class.getDeclaredField("id");
            fId.setAccessible(true);
            fId.set(carrera, carreraId);
        } catch (Exception ignored) {}

        Testimonio t1 = new Testimonio();
        t1.setCarrera(carrera);
        t1.setIdUsuario(UUID.randomUUID());
        t1.setTextoTestimonio("Muy buena carrera");
        t1.setAprobado(true);
        // simular timestamps
        try {
            var fCreado = Testimonio.class.getDeclaredField("creadoEn");
            fCreado.setAccessible(true);
            fCreado.set(t1, LocalDateTime.now());
            var fId = Testimonio.class.getDeclaredField("id");
            fId.setAccessible(true);
            fId.set(t1, "t1");
        } catch (Exception ignored) {}

        when(carreraRepository.findById(carreraId)).thenReturn(Optional.of(carrera));
        when(testimonioRepository.findByCarrera_IdAndAprobadoTrueOrderByCreadoEnDesc(carreraId))
                .thenReturn(List.of(t1));

        List<TestimonioResponse> resp = testimonioService.obtenerAprobadosPorCarrera(carreraId);
        assertThat(resp).hasSize(1);
        assertThat(resp.getFirst().getIdCarrera()).isEqualTo(carreraId);
        assertThat(resp.getFirst().getTextoTestimonio()).isEqualTo("Muy buena carrera");
        assertThat(resp.getFirst().isAprobado()).isTrue();
    }

    @Test
    @DisplayName("obtenerAprobadosPorCarrera devuelve lista vacía cuando no hay testimonios")
    void obtenerAprobados_empty() {
        Long carreraId = 2L;
        Carrera carrera = new Carrera();
        try {
            var fId = Carrera.class.getDeclaredField("id");
            fId.setAccessible(true);
            fId.set(carrera, carreraId);
        } catch (Exception ignored) {}

        when(carreraRepository.findById(carreraId)).thenReturn(Optional.of(carrera));
        when(testimonioRepository.findByCarrera_IdAndAprobadoTrueOrderByCreadoEnDesc(carreraId))
                .thenReturn(List.of());

        List<TestimonioResponse> resp = testimonioService.obtenerAprobadosPorCarrera(carreraId);
        assertThat(resp).isEmpty();
    }
}
