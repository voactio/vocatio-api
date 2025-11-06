package com.vocatio.unit;

import com.vocatio.dto.response.RecursoResponse;
import com.vocatio.exception.BadRequestException;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.model.Recurso;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.RecursoRepository;
import com.vocatio.service.RecursosService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecursosServiceTest {

    @Mock
    CarreraRepository carreraRepository;

    @Mock
    RecursoRepository recursoRepository;

    @InjectMocks
    RecursosService recursosService;

    @Test
    @DisplayName("listarPorCarrera lanza BadRequestException cuando id es nulo o <= 0")
    void listarPorCarrera_idInvalido() {
        assertThatThrownBy(() -> recursosService.listarPorCarrera(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El id de la carrera no es válido.");

        verifyNoInteractions(carreraRepository, recursoRepository);
    }

    @Test
    @DisplayName("listarPorCarrera lanza ResourceNotFoundException cuando la carrera no existe")
    void listarPorCarrera_carreraNoExiste() {
        when(carreraRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recursosService.listarPorCarrera(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se encontró la carrera indicada.");

        verify(carreraRepository).findById(10L);
        verifyNoInteractions(recursoRepository);
    }

    @Test
    @DisplayName("listarPorCarrera lanza ResourceNotFoundException cuando la carrera no tiene recursos")
    void listarPorCarrera_sinRecursos() {
        Carrera carrera = mock(Carrera.class);
        when(carreraRepository.findById(5L)).thenReturn(Optional.of(carrera));
        when(carrera.getId()).thenReturn(5L);

        when(recursoRepository.findByCarrera(5L)).thenReturn(List.of());

        assertThatThrownBy(() -> recursosService.listarPorCarrera(5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("La carrera no tiene recursos digitales registrados.");

        verify(carreraRepository).findById(5L);
        verify(recursoRepository).findByCarrera(5L);
    }

    @Test
    @DisplayName("listarPorCarrera devuelve la lista mapeada cuando hay recursos")
    void listarPorCarrera_ok() {
        Carrera carrera = mock(Carrera.class);
        when(carreraRepository.findById(2L)).thenReturn(Optional.of(carrera));
        when(carrera.getId()).thenReturn(2L);

        Recurso r1 = mock(Recurso.class);
        when(r1.getTitulo()).thenReturn("Introducción a IA");
        when(r1.getTipoRecurso()).thenReturn("PDF");
        when(r1.getAutor()).thenReturn("John Doe");
        when(r1.getUrl()).thenReturn("https://example.com/ia.pdf");

        Recurso r2 = mock(Recurso.class);
        when(r2.getTitulo()).thenReturn("Curso de Redes");
        when(r2.getTipoRecurso()).thenReturn("VIDEO");
        when(r2.getAutor()).thenReturn("Jane Smith");
        when(r2.getUrl()).thenReturn("https://example.com/redes");

        when(recursoRepository.findByCarrera(2L))
                .thenReturn(List.of(r1, r2));

        List<RecursoResponse> resp = recursosService.listarPorCarrera(2L);

        assertThat(resp).hasSize(2);
        assertThat(resp.get(0).getTitulo()).isEqualTo("Introducción a IA");
        assertThat(resp.get(0).getTipoRecurso()).isEqualTo("PDF");
        assertThat(resp.get(0).getAutor()).isEqualTo("John Doe");
        assertThat(resp.get(0).getUrl()).isEqualTo("https://example.com/ia.pdf");

        verify(carreraRepository).findById(2L);
        verify(recursoRepository).findByCarrera(2L);
    }
}