package com.vocatio.unit;

import com.vocatio.dto.request.UniversitiesByCareerRequest;
import com.vocatio.dto.response.UniversitiesByCareerResponse;
import com.vocatio.exception.BadRequestException;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.model.Universidad;
import com.vocatio.model.UniversidadCarrera;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.UniversidadCarreraRepository;
import com.vocatio.service.UniversidadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniversidadServiceTest {

    @Mock
    CarreraRepository carreraRepository;

    @Mock
    UniversidadCarreraRepository universidadCarreraRepository;

    @InjectMocks
    UniversidadService universidadService;

    @Test
    @DisplayName("obtener() lanza BadRequestException cuando idCarrera es nulo o <= 0")
    void obtener_idInvalido() {
        var req = new UniversitiesByCareerRequest();
        req.setIdCarrera(0L);

        assertThatThrownBy(() -> universidadService.obtener(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Debes enviar un idCarrera válido.");

        verifyNoInteractions(carreraRepository, universidadCarreraRepository);
    }

    @Test
    @DisplayName("obtener() lanza ResourceNotFoundException cuando la carrera no existe")
    void obtener_carreraNoExiste() {
        var req = new UniversitiesByCareerRequest();
        req.setIdCarrera(10L);

        when(carreraRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> universidadService.obtener(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se encontró la carrera indicada.");

        verify(carreraRepository).findById(10L);
        verifyNoInteractions(universidadCarreraRepository);
    }

    @Test
    @DisplayName("obtener() lanza ResourceNotFoundException cuando no hay universidades asociadas")
    void obtener_sinUniversidades() {
        var req = new UniversitiesByCareerRequest();
        req.setIdCarrera(5L);

        // solo necesitamos que exista una carrera, no más getters
        Carrera carrera = mock(Carrera.class);
        when(carreraRepository.findById(5L)).thenReturn(Optional.of(carrera));

        when(universidadCarreraRepository.findAllByCarrera(5L))
                .thenReturn(List.of());

        assertThatThrownBy(() -> universidadService.obtener(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No hay universidades asociadas a esta carrera.");

        verify(carreraRepository).findById(5L);
        verify(universidadCarreraRepository).findAllByCarrera(5L);
    }

    @Test
    @DisplayName("obtener() devuelve el response con la universidad mapeada")
    void obtener_ok() {
        var req = new UniversitiesByCareerRequest();
        req.setIdCarrera(2L);

        // carrera mock
        Carrera carrera = mock(Carrera.class);
        when(carrera.getId()).thenReturn(2L);
        when(carrera.getNombre()).thenReturn("Ingeniería Industrial");
        when(carrera.getDuracionAnios()).thenReturn(5);
        when(carreraRepository.findById(2L)).thenReturn(Optional.of(carrera));

        // universidad mock
        Universidad uni = mock(Universidad.class);
        when(uni.getId()).thenReturn(100L);
        when(uni.getNombre()).thenReturn("Universidad Nacional");
        when(uni.getUbicacion()).thenReturn("Lima");
        when(uni.getUrlSitioWeb()).thenReturn("https://univ.example");

        // relación mock
        UniversidadCarrera uc = mock(UniversidadCarrera.class);
        when(uc.getUniversidad()).thenReturn(uni);
        // este sí lo usa el servicio
        when(uc.getCostoPorAnio()).thenReturn(BigDecimal.valueOf(7500));
        when(uc.getUrlPlanEspecifico()).thenReturn("https://univ.example/plan-industrial");
        // OJO: NO ponemos when(uc.getCarrera()) porque no lo usa

        when(universidadCarreraRepository.findAllByCarrera(2L))
                .thenReturn(List.of(uc));

        // ejecutar
        UniversitiesByCareerResponse resp = universidadService.obtener(req);

        // asserts
        assertThat(resp.getIdCarrera()).isEqualTo(2L);
        assertThat(resp.getNombreCarrera()).isEqualTo("Ingeniería Industrial");
        assertThat(resp.getUniversidades()).hasSize(1);

        var item = resp.getUniversidades().get(0);
        assertThat(item.getIdUniversidad()).isEqualTo(100L);
        assertThat(item.getNombreUniversidad()).isEqualTo("Universidad Nacional");
        assertThat(item.getUbicacion()).isEqualTo("Lima");
        assertThat(item.getDuracionAnios()).isEqualTo(5);
        assertThat(item.getCostoPorAnio()).isEqualTo(BigDecimal.valueOf(7500));
        assertThat(item.getUrlUniversidad()).isEqualTo("https://univ.example");
        assertThat(item.getUrlPlanEspecifico()).isEqualTo("https://univ.example/plan-industrial");

        verify(carreraRepository).findById(2L);
        verify(universidadCarreraRepository).findAllByCarrera(2L);
    }
}
