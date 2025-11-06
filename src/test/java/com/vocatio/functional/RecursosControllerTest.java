package com.vocatio.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocatio.controller.RecursosController;
import com.vocatio.dto.response.RecursoResponse;
import com.vocatio.security.JwtAuthenticationFilter;
import com.vocatio.security.JwtUtil;
import com.vocatio.service.CustomUserDetailsService;
import com.vocatio.service.RecursosService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecursosController.class)
@Import({JwtAuthenticationFilter.class, RecursosControllerTest.TestSecurityConfig.class})
class RecursosControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RecursosService recursosService;

    // mocks para el filtro
    @MockitoBean
    JwtUtil jwtUtil;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    // seguridad solo para el test
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http,
                                        JwtAuthenticationFilter jwtFilter) throws Exception {
            http.csrf(csrf -> csrf.disable());
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/carreras/**").authenticated()
                    .anyRequest().permitAll()
            );
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
    }

    @Test
    @DisplayName("GET /carreras/{id}/recursos con JWT válido -> 200")
    void listarRecursos_conJwt_ok() throws Exception {
        String token = "fake-token";
        String email = "user@test.com";

        // lo que usa el filtro
        Mockito.when(jwtUtil.validateToken(token)).thenReturn(true);
        Mockito.when(jwtUtil.getEmailFromToken(token)).thenReturn(email);

        User userDetails = new User(
                email,
                "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Mockito.when(customUserDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        // lo que devuelve el service
        List<RecursoResponse> mockResp = List.of(
                RecursoResponse.builder()
                        .titulo("Libro de algoritmos")
                        .tipoRecurso("PDF")
                        .autor("CLRS")
                        .url("https://example.com/algoritmos")
                        .build()
        );
        Mockito.when(recursosService.listarPorCarrera(1L))
                .thenReturn(mockResp);

        mockMvc.perform(
                        get("/carreras/{idCarrera}/recursos", 1L)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Libro de algoritmos"));
    }

    @Test
    @DisplayName("GET /carreras/{id}/recursos sin JWT -> 403")
    void listarRecursos_sinJwt_403() throws Exception {
        mockMvc.perform(
                        get("/carreras/{idCarrera}/recursos", 1L)
                )
                .andExpect(status().isForbidden());
    }
}
