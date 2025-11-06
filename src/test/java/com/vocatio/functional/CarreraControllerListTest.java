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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarreraController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CarreraController - Listado (funcional)")
class CarreraControllerListTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarreraService carreraService;

    // Mockeos para evitar fallos al cargar seguridad en el contexto de prueba
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /carreras/listado - 200, delega en servicio, retorna lista")
    void listado_ok() throws Exception {
        int page = 0;
        int size = 2;
        List<CarreraCardResponse> mockList = List.of(
                new CarreraCardResponse(1L, "Ing. Sistemas", "Desc corta 1"),
                new CarreraCardResponse(2L, "Medicina", "Desc corta 2")
        );

        when(carreraService.listarInicial(eq(page), eq(size))).thenReturn(mockList);

        mockMvc.perform(get("/carreras/listado")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ing. Sistemas"))
                .andExpect(jsonPath("$[0].descripcionCorta").value("Desc corta 1"));

        verify(carreraService, times(1)).listarInicial(page, size);
        verifyNoMoreInteractions(carreraService);
    }
}
