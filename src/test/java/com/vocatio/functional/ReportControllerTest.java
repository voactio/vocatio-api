package com.vocatio.functional;

import com.vocatio.controller.ReportController;
import com.vocatio.dto.response.CarreraAfinDto;
import com.vocatio.dto.response.GenerateReportResponse;
import com.vocatio.exception.ResourceNorFoundException; // ⚠️ cambia al nombre real si es "Not"
import com.vocatio.security.JwtAuthenticationFilter;
import com.vocatio.security.JwtUtil;
import com.vocatio.service.CustomUserDetailsService;
import com.vocatio.service.ReportService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReportController.class)
@Import({JwtAuthenticationFilter.class, ReportControllerTest.TestSecurityConfig.class})
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http,
                                        JwtAuthenticationFilter jwtFilter) throws Exception {
            http.csrf(csrf -> csrf.disable());
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/resultados/**").authenticated()
                    .anyRequest().permitAll()
            );
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
    }

    private void mockJwtOk(String token, String email) {
        Mockito.when(jwtUtil.validateToken(token)).thenReturn(true);
        Mockito.when(jwtUtil.getEmailFromToken(token)).thenReturn(email);

        User userDetails = new User(
                email,
                "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Mockito.when(customUserDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);
    }

    @Test
    @DisplayName("GET /api/resultados/{id}/detalle con JWT válido -> 200 y JSON")
    void obtenerDetalle_ok() throws Exception {
        String token = "mi-token-fake";
        String email = "user@test.com";
        mockJwtOk(token, email);

        Long idResultado = 3L;
        UUID idUsuario = UUID.fromString("5e58a502-77c2-4f75-9a6c-cfd9c3afbd8a");

        GenerateReportResponse mockResp = GenerateReportResponse.builder()
                .idResultado(idResultado)
                .completadoEn(LocalDateTime.now())
                .puntajes(Map.of(
                        "Ciberseguridad", 9,
                        "Sistemas y redes", 7
                ))
                .topCarreras(List.of(
                        CarreraAfinDto.builder()
                                .id(11L)
                                .nombre("Ingeniería en Ciberseguridad")
                                .descripcion("Protección de sistemas, auditoría y respuesta a incidentes.")
                                .areaInteres("Ciberseguridad")
                                .porcentajeCompatibilidad(100.0)
                                .build()
                ))
                .build();

        Mockito.when(reportService.obtenerResultadoConRecomendaciones(idResultado, idUsuario))
                .thenReturn(mockResp);

        mockMvc.perform(
                        get("/api/resultados/{id}/detalle", idResultado)
                                .header("Authorization", "Bearer " + token)
                                .param("usuario", idUsuario.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idResultado").value(idResultado))
                .andExpect(jsonPath("$.puntajes.Ciberseguridad").value(9))
                .andExpect(jsonPath("$.topCarreras[0].nombre").value("Ingeniería en Ciberseguridad"));

        Mockito.verify(reportService).obtenerResultadoConRecomendaciones(
                eq(idResultado),
                eq(idUsuario)
        );
    }

    @Test
    @DisplayName("GET /api/resultados/{id}/detalle con JWT válido pero sin resultado -> 404")
    void obtenerDetalle_notFound() throws Exception {
        String token = "mi-token-fake";
        String email = "user@test.com";
        mockJwtOk(token, email);

        Long idResultado = 999L;
        UUID idUsuario = UUID.randomUUID();

        Mockito.when(reportService.obtenerResultadoConRecomendaciones(idResultado, idUsuario))
                .thenThrow(new ResourceNorFoundException("Resultado no encontrado o no pertenece al usuario"));

        mockMvc.perform(
                        get("/api/resultados/{id}/detalle", idResultado)
                                .header("Authorization", "Bearer " + token)
                                .param("usuario", idUsuario.toString())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/resultados/{id}/pdf con JWT válido -> 200 y bytes PDF")
    void descargarPdf_ok() throws Exception {
        String token = "mi-token-fake";
        String email = "user@test.com";
        mockJwtOk(token, email);

        Long idResultado = 3L;
        UUID idUsuario = UUID.randomUUID();

        byte[] fakePdf = "pdf-fake".getBytes();
        Mockito.when(reportService.generarPdfResultado(idResultado, idUsuario))
                .thenReturn(fakePdf);

        mockMvc.perform(
                        get("/api/resultados/{id}/pdf", idResultado)
                                .header("Authorization", "Bearer " + token)
                                .param("usuario", idUsuario.toString())
                )
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=resultado-" + idResultado + ".pdf"))
                .andExpect(content().bytes(fakePdf));

        Mockito.verify(reportService).generarPdfResultado(
                eq(idResultado),
                eq(idUsuario)
        );
    }
}
