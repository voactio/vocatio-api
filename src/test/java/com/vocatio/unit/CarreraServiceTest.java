package com.vocatio.unit;

import com.vocatio.dto.response.CarreraCardResponse;
import com.vocatio.dto.response.CarreraDetailResponse;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.service.CarreraService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarreraService - Unit")
class CarreraServiceTest {

    @Mock
    private CarreraRepository carreraRepository;

    @InjectMocks
    private CarreraService carreraService;

    private Carrera carreraDummy() {
        Carrera c = new Carrera();
        c.setNombre("Ingeniería de Sistemas");
        c.setDescripcion("Descripción muy larga de la carrera que debería recortarse en el listado para no exceder el máximo permitido por la tarjeta.");
        c.setDuracionAnios(5);
        c.setModalidad("Presencial");
        c.setPerfilRiasec("RIASEC-I");
        c.setRangoSalarioPromedio("3000-6000");
        // Simular timestamps
        try {
            var creado = LocalDateTime.of(2024, 1, 1, 0, 0);
            var actualizado = LocalDateTime.of(2024, 6, 1, 0, 0);
            // mediante reflexión por ser campos gestionados por callbacks
            var fCreado = Carrera.class.getDeclaredField("creadoEn");
            fCreado.setAccessible(true);
            fCreado.set(c, creado);
            var fAct = Carrera.class.getDeclaredField("actualizadoEn");
            fAct.setAccessible(true);
            fAct.set(c, actualizado);
        } catch (Exception ignored) {}
        // id
        try {
            var fId = Carrera.class.getDeclaredField("id");
            fId.setAccessible(true);
            fId.set(c, 1L);
        } catch (Exception ignored) {}
        return c;
    }

    @Test
    @DisplayName("listarInicial devuelve tarjetas mapeadas")
    void listarInicial_ok() {
        Carrera c = carreraDummy();
        Page<Carrera> page = new PageImpl<>(List.of(c), PageRequest.of(0, 12), 1);
        when(carreraRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<CarreraCardResponse> resp = carreraService.listarInicial(0, 12);
        assertThat(resp).hasSize(1);
        assertThat(resp.get(0).getId()).isEqualTo(1L);
        assertThat(resp.get(0).getNombre()).isEqualTo("Ingeniería de Sistemas");
        assertThat(resp.get(0).getDescripcionCorta()).isNotBlank();
    }

    @Test
    @DisplayName("obtenerDetalle retorna DTO cuando existe")
    void obtenerDetalle_ok() {
        Carrera c = carreraDummy();
        when(carreraRepository.findById(1L)).thenReturn(Optional.of(c));

        CarreraDetailResponse dto = carreraService.obtenerDetalle(1L);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNombre()).isEqualTo("Ingeniería de Sistemas");
        assertThat(dto.getModalidad()).isEqualTo("Presencial");
        assertThat(dto.getRangoSalarioPromedio()).isEqualTo("3000-6000");
    }

    @Test
    @DisplayName("obtenerDetalle lanza 404 si no existe")
    void obtenerDetalle_404() {
        when(carreraRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> carreraService.obtenerDetalle(999L));
    }

    @Test
    @DisplayName("buscar aplica filtros y mapea tarjetas")
    void buscar_ok() {
        Carrera c = carreraDummy();
        Page<Carrera> page = new PageImpl<>(List.of(c), PageRequest.of(1, 5), 1);
        when(carreraRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        List<CarreraCardResponse> resp = carreraService.buscar("inge", "Presencial", "RIASEC-I", 1, 5, "nombre,desc");
        assertThat(resp).hasSize(1);
        assertThat(resp.get(0).getNombre()).contains("Ingeniería");
    }
}
