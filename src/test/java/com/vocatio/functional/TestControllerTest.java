package com.vocatio.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocatio.controller.TestController;
import com.vocatio.dto.request.SubmitAnswerRequestDTO;
import com.vocatio.dto.response.*;
import com.vocatio.exception.BadRequestException;
import com.vocatio.security.JwtAuthenticationFilter;
import com.vocatio.security.JwtUtil;
import com.vocatio.service.CustomUserDetailsService;
import com.vocatio.service.TestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TestController.class)
@Import({JwtAuthenticationFilter.class, TestControllerTest.TestSecurityConfig.class})
@DisplayName("TestController - Pruebas Funcionales (US11 y US12)")
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TestService testService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // CONFIGURACIÓN DE SEGURIDAD DE PRUEBA
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
            http.csrf(csrf -> csrf.disable());
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/tests/**").authenticated()
                    .anyRequest().permitAll()
            );
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
    }

    private void mockAuthenticatedUser(String token, UUID userId) {
        String email = "test@vocatio.com";

        Mockito.when(jwtUtil.validateToken(token)).thenReturn(true);
        Mockito.when(jwtUtil.getEmailFromToken(token)).thenReturn(email);

        User userDetails = new User(email, "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Mockito.when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        Mockito.when(jwtUtil.getUserIdFromToken(token)).thenReturn(userId);
    }

    // US11: INICIAR TEST

    @Test
    @DisplayName("POST /tests/{id}/iniciar - 200 OK: Inicia correctamente")
    void iniciarTest_Ok() throws Exception {
        String token = "token-valido";
        UUID userId = UUID.randomUUID();
        Long testId = 1L;

        mockAuthenticatedUser(token, userId);

        StartTestResponseDTO mockResponse = new StartTestResponseDTO();
        mockResponse.setSessionId(100L);
        PreguntaDTO preg = new PreguntaDTO();
        preg.setTextoPregunta("¿Pregunta 1?");
        mockResponse.setPrimeraPregunta(preg);

        Mockito.when(testService.iniciarTest(testId, userId)).thenReturn(mockResponse);

        mockMvc.perform(post("/tests/{id}/iniciar", testId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(100L))
                .andExpect(jsonPath("$.primeraPregunta.textoPregunta").value("¿Pregunta 1?"));
    }

    @Test
    @DisplayName("POST /tests/{id}/iniciar - 403 Forbidden: Sin token")
    void iniciarTest_SinToken() throws Exception {
        mockMvc.perform(post("/tests/1/iniciar"))
                .andExpect(status().isForbidden());
    }

    // US11: RESPONDER PREGUNTA

    @Test
    @DisplayName("POST /tests/sessions/{id}/answers - 200 OK: Devuelve siguiente pregunta")
    void submitAnswer_SiguientePregunta() throws Exception {
        String token = "token-valido";
        UUID userId = UUID.randomUUID();
        mockAuthenticatedUser(token, userId);

        Long sessionId = 100L;
        SubmitAnswerRequestDTO request = new SubmitAnswerRequestDTO();
        request.setPreguntaId(1L);
        request.setOpcionId(5L);

        PreguntaDTO nextPregunta = new PreguntaDTO();
        nextPregunta.setId(2L);
        nextPregunta.setTextoPregunta("¿Siguiente?");

        Mockito.when(testService.submitAnswerAndGetNext(eq(sessionId), any(SubmitAnswerRequestDTO.class)))
                .thenReturn(nextPregunta);

        mockMvc.perform(post("/tests/sessions/{id}/answers", sessionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.textoPregunta").value("¿Siguiente?"));
    }

    @Test
    @DisplayName("POST /tests/sessions/{id}/answers - 400 Bad Request: Opción inválida")
    void submitAnswer_BadRequest() throws Exception {
        String token = "token-valido";
        UUID userId = UUID.randomUUID();
        mockAuthenticatedUser(token, userId);

        Long sessionId = 100L;
        SubmitAnswerRequestDTO request = new SubmitAnswerRequestDTO();

        Mockito.when(testService.submitAnswerAndGetNext(eq(sessionId), any()))
                .thenThrow(new BadRequestException("Opción inválida"));

        mockMvc.perform(post("/tests/sessions/{id}/answers", sessionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // US12: VER RESULTADOS

    @Test
    @DisplayName("GET /tests/sessions/{id}/results - 400 Bad Request: Test no finalizado")
    void getResults_NoFinalizado() throws Exception {
        String token = "token-valido";
        UUID userId = UUID.randomUUID();
        mockAuthenticatedUser(token, userId);

        Long sessionId = 100L;

        Mockito.when(testService.getTestResults(sessionId))
                .thenThrow(new BadRequestException("El test no ha finalizado"));

        mockMvc.perform(get("/tests/sessions/{id}/results", sessionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }
}