package com.vocatio.controller;

import com.vocatio.dto.TestimonioResponseDTO;
import com.vocatio.service.TestimonioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TestimonioController.class)
@ActiveProfiles("test")
class TestimonioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestimonioService testimonioService;

    @Test
    void deberiaRetornar200YInvocarServicioConCarreraId() throws Exception {
        Long carreraId = 1L;
        List<TestimonioResponseDTO> mockResult = List.of(
                new TestimonioResponseDTO(1L, "Contenido 1", "Autor 1"),
                new TestimonioResponseDTO(2L, "Contenido 2", "Autor 2")
        );

        when(testimonioService.obtenerTestimoniosPorCarrera(carreraId)).thenReturn(mockResult);

        mockMvc.perform(get("/carreras/{carreraId}/testimonios", carreraId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].contenido").value("Contenido 1"))
                .andExpect(jsonPath("$[0].nombreAutor").value("Autor 1"));

        verify(testimonioService, times(1)).obtenerTestimoniosPorCarrera(carreraId);
        verifyNoMoreInteractions(testimonioService);
    }
}
