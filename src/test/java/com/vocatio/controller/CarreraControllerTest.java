package com.vocatio.controller;

import com.vocatio.dto.CarreraResponseDTO;
import com.vocatio.service.CarreraService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CarreraController.class)
class CarreraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarreraService carreraService;

    @Test
    @DisplayName("GET /api/carreras responde 200 y delega en el servicio con paginación")
    void listarDebeResponderOk() throws Exception {
        int page = 1;
        int limit = 5;
        Page<CarreraResponseDTO> vacia = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, limit), 0);
        Mockito.when(carreraService.obtenerListadoPaginado(eq(page), eq(limit))).thenReturn(vacia);

        mockMvc.perform(get("/api/carreras")
                        .param("page", String.valueOf(page))
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk());

        Mockito.verify(carreraService).obtenerListadoPaginado(page, limit);
    }
}

