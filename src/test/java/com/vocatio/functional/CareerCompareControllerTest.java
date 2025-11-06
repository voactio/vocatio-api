package com.vocatio.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocatio.controller.CareerCompareController;
import com.vocatio.dto.request.CompareCareersRequest;
import com.vocatio.dto.response.CompareCareersResponse;
import com.vocatio.security.JwtAuthenticationFilter;
import com.vocatio.security.JwtUtil;
import com.vocatio.service.CareerCompareService;
import com.vocatio.service.CustomUserDetailsService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CareerCompareController.class)
// metemos nuestro filtro + nuestra config de seguridad de prueba
@Import({JwtAuthenticationFilter.class, CareerCompareControllerTest.TestSecurityConfig.class})
class CareerCompareControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // mock del servicio que usa el controller
    @MockitoBean
    CareerCompareService careerCompareService;

    // mocks que usa el filtro
    @MockitoBean
    JwtUtil jwtUtil;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    // ====== CONFIG DE SEGURIDAD SOLO PARA ESTE TEST ======
    // protege /api/carreras/comparar y agrega nuestro filtro JWT
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http,
                                        JwtAuthenticationFilter jwtFilter) throws Exception {
            http.csrf(csrf -> csrf.disable());
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/carreras/comparar").authenticated()
                    .anyRequest().permitAll()
            );
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
    }

    @Test
    @DisplayName("POST /api/carreras/comparar con JWT válido -> 200")
    void compararCarreras_conJwt_ok() throws Exception {
        String token = "mi-token-fake";
        String email = "user@test.com";

        // lo que va a usar el filtro
        Mockito.when(jwtUtil.validateToken(token)).thenReturn(true);
        Mockito.when(jwtUtil.getEmailFromToken(token)).thenReturn(email);

        User userDetails = new User(
                email,
                "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Mockito.when(customUserDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        // lo que devuelve el servicio de negocio
        CompareCareersResponse fakeResp = CompareCareersResponse.builder()
                .carrera1(CompareCareersResponse.CareerCompareItem.builder()
                        .id(1L).nombre("Ing. Sistemas").build())
                .carrera2(CompareCareersResponse.CareerCompareItem.builder()
                        .id(4L).nombre("Ing. Software").build())
                .build();
        Mockito.when(careerCompareService.comparar(any(CompareCareersRequest.class)))
                .thenReturn(fakeResp);

        // body
        CompareCareersRequest req = new CompareCareersRequest();
        req.setIdUsuario(UUID.randomUUID());
        req.setIdResultado(4L);
        req.setIdCarrera1(1L);
        req.setIdCarrera2(4L);

        mockMvc.perform(
                        post("/api/carreras/comparar")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(req))
                                .with(csrf()) // por si tu config añade csrf en algún punto
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carrera1.id").value(1L))
                .andExpect(jsonPath("$.carrera2.id").value(4L));
    }

    @Test
    @DisplayName("POST /api/carreras/comparar sin JWT -> 403")
    void compararCarreras_sinJwt_403() throws Exception {
        CompareCareersRequest req = new CompareCareersRequest();
        req.setIdUsuario(UUID.randomUUID());
        req.setIdResultado(4L);
        req.setIdCarrera1(1L);
        req.setIdCarrera2(4L);

        mockMvc.perform(
                        post("/api/carreras/comparar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(req))
                                .with(csrf())
                )
                .andExpect(status().isForbidden()); // 403
    }
}
