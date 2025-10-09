package com.vocatio.controller;

import com.vocatio.dto.CarreraDetailDTO;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.service.CarreraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarreraController.class)
@TestPropertySource(properties = "server.servlet.context-path=/api/v1")
class CarreraControllerDetailTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarreraService carreraService;

    @Test
    void detalleCarrera_valido_devuelve200() throws Exception {
        Long id = 1L;
        CarreraDetailDTO dto = new CarreraDetailDTO(
                id,
                "Ingeniería de Sistemas",
                5,
                "Presencial",
                "Descripción de la carrera",
                "Plan de estudios...",
                List.of("Uni A", "Uni B")
        );
        when(carreraService.obtenerDetalleCarrera(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/carreras/{id}", id)
                        .contextPath("/api/v1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ingeniería de Sistemas"))
                .andExpect(jsonPath("$.duracionAnios").value(5))
                .andExpect(jsonPath("$.modalidad").value("Presencial"))
                .andExpect(jsonPath("$.descripcion").value("Descripción de la carrera"))
                .andExpect(jsonPath("$.planEstudios").value("Plan de estudios..."))
                .andExpect(jsonPath("$.universidadesSugeridas.length()").value(2));
    }

    @Test
    void detalleCarrera_invalido_devuelve404() throws Exception {
        Long id = 999L;
        when(carreraService.obtenerDetalleCarrera(id)).thenThrow(new ResourceNotFoundException("Carrera no encontrada"));

        mockMvc.perform(get("/api/v1/carreras/{id}", id)
                        .contextPath("/api/v1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
