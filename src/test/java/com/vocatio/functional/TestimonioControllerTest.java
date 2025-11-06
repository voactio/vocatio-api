// java
package com.vocatio.functional;

import com.vocatio.controller.TestimonioController;
import com.vocatio.security.JwtUtil;
import com.vocatio.service.CustomUserDetailsService;
import com.vocatio.service.TestimonioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TestimonioController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TestimonioController - Pruebas funcionales")
class TestimonioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TestimonioService testimonioService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /carreras/{carreraId}/testimonios - 200 y delega en servicio")
    void deberiaRetornar200YDelegarEnServicio() throws Exception {
        Long carreraId = 1L;
        when(testimonioService.obtenerAprobadosPorCarrera(carreraId))
                .thenReturn(List.of()); // respuesta mínima para no acoplarse al DTO

        mockMvc.perform(get("/carreras/{carreraId}/testimonios", carreraId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));

        verify(testimonioService, times(1)).obtenerAprobadosPorCarrera(carreraId);
        verifyNoMoreInteractions(testimonioService);
    }
}
