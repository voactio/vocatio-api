package com.vocatio.functional;

import com.vocatio.controller.CarreraController;
import com.vocatio.dto.response.CarreraDetailResponse;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.security.JwtUtil;
import com.vocatio.service.CarreraService;
import com.vocatio.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarreraController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CarreraController - Detalle (funcional)")
class CarreraControllerDetailFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarreraService carreraService;

    // Seguridad mockeada
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /carreras/{id} - 200 OK devuelve el detalle")
    void detalle_ok() throws Exception {
        Long id = 1L;
        var dto = new CarreraDetailResponse(
                id,
                "Ingeniería de Sistemas",
                "Descripción",
                5,
                "Presencial",
                "3000-6000",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(carreraService.obtenerDetalle(id)).thenReturn(dto);

        mockMvc.perform(get("/carreras/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ingeniería de Sistemas"))
                .andExpect(jsonPath("$.modalidad").value("Presencial"));

        verify(carreraService, times(1)).obtenerDetalle(id);
        verifyNoMoreInteractions(carreraService);
    }

    @Test
    @DisplayName("GET /carreras/{id} - 404 cuando no existe")
    void detalle_404() throws Exception {
        Long id = 999L;
        when(carreraService.obtenerDetalle(id)).thenThrow(new ResourceNotFoundException("Carrera no encontrada"));

        mockMvc.perform(get("/carreras/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(carreraService, times(1)).obtenerDetalle(id);
        verifyNoMoreInteractions(carreraService);
    }
}
