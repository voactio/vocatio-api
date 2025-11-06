package com.vocatio.functional;

import com.vocatio.controller.CarreraController;
import com.vocatio.dto.response.CarreraCardResponse;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarreraController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CarreraController - Búsqueda con filtros (funcional)")
class CarreraControllerSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarreraService carreraService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /carreras - 200, aplica filtros y paginación")
    void buscar_ok() throws Exception {
        List<CarreraCardResponse> mockList = List.of(
                new CarreraCardResponse(10L, "Psicología", "Desc"));

        when(carreraService.buscar(eq("psi"), eq("Presencial"), eq("RIASEC-I"), eq(1), eq(5), eq("nombre,desc")))
                .thenReturn(mockList);

        mockMvc.perform(get("/carreras")
                        .param("nombre", "psi")
                        .param("modalidad", "Presencial")
                        .param("perfilRiasec", "RIASEC-I")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "nombre,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10));

        verify(carreraService, times(1)).buscar("psi", "Presencial", "RIASEC-I", 1, 5, "nombre,desc");
        verifyNoMoreInteractions(carreraService);
    }
}
