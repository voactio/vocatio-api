package com.vocatio.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocatio.controller.RecuperacionController;
import com.vocatio.service.RecuperacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(RecuperacionController.class)
@DisplayName("RecuperacionController - Pruebas funcionales")
public class RecuperacionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RecuperacionService recuperacionService;

    private RecuperacionController recuperacionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recuperacionController = new RecuperacionController(recuperacionService);
        mockMvc = MockMvcBuilders.standaloneSetup(recuperacionController).build();
    }

    // OlvidoContraseña - Exitoso
    @Test
    @DisplayName("POST /recuperacion/olvidoContra - exitoso")
    void olvidoContraExitoso() throws Exception {
        Map<String, String> body = Map.of("correo", "test@vocatio.com");
        ResponseEntity<Map<String, String>> entity = ResponseEntity.ok(Map.of("mensaje", "Correo enviado correctamente"));

        when(recuperacionService.olvidoContrasena(anyString()))
                .thenReturn((ResponseEntity) entity);

        mockMvc.perform(post("/recuperacion/olvidoContra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Correo enviado correctamente"));
    }

    // OlvidoContraseña - Fallo
    @Test
    @DisplayName("POST /recuperacion/olvidoContra - fallo")
    void olvidoContraFallo() throws Exception {
        Map<String, String> body = Map.of("correo", "noexiste@vocatio.com");
        ResponseEntity<Map<String, String>> entity = ResponseEntity.badRequest().body(Map.of("error", "Correo no registrado"));

        when(recuperacionService.olvidoContrasena(anyString()))
                .thenReturn((ResponseEntity) entity);

        mockMvc.perform(post("/recuperacion/olvidoContra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Correo no registrado"));
    }

    // Validar Token - Exitoso
    @Test
    @DisplayName("GET /recuperacion/reestablecerContra - exitoso")
    void validarTokenExitoso() throws Exception {
        String token = "tokenValido";
        ResponseEntity<Map<String, String>> entity = ResponseEntity.ok(Map.of("mensaje", "Token válido"));

        when(recuperacionService.validarToken(token))
                .thenReturn((ResponseEntity) entity);

        mockMvc.perform(get("/recuperacion/reestablecerContra")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Token válido"));
    }

    // Validar Token - Fallo
    @Test
    @DisplayName("GET /recuperacion/reestablecerContra - fallo")
    void validarTokenFallo() throws Exception {
        String token = "tokenInvalido";
        ResponseEntity<Map<String, String>> entity = ResponseEntity.badRequest().body(Map.of("error", "Token inválido o expirado"));

        when(recuperacionService.validarToken(token))
                .thenReturn((ResponseEntity) entity);

        mockMvc.perform(get("/recuperacion/reestablecerContra")
                        .param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Token inválido o expirado"));
    }

    // Reestablecer contraseña - Exitoso
    @Test
    @DisplayName("POST /recuperacion/reestablecerContra - exitoso")
    void reestablecerContraExitoso() throws Exception {
        Map<String, String> body = Map.of(
                "token", "tokenValido",
                "nuevaContrasena", "Password123",
                "confirmarContrasena", "Password123"
        );
        ResponseEntity<Map<String, String>> entity = ResponseEntity.ok(Map.of("mensaje", "Contraseña reestablecida correctamente"));

        when(recuperacionService.reestablecerContrasena(anyString(), anyString(), anyString()))
                .thenReturn((ResponseEntity) entity);

        mockMvc.perform(post("/recuperacion/reestablecerContra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Contraseña reestablecida correctamente"));
    }

    // Reestablecer contraseña - Fallo por contraseñas distintas
    @Test
    @DisplayName("POST /recuperacion/reestablecerContra - fallo por contraseñas distintas")
    void reestablecerContraFalloContraseñasDistintas() throws Exception {
        Map<String, String> body = Map.of(
                "token", "tokenValido",
                "nuevaContrasena", "Password123",
                "confirmarContrasena", "Password456"
        );
        ResponseEntity<Map<String, String>> entity = ResponseEntity.badRequest().body(Map.of("error", "Las contraseñas no coinciden"));

        when(recuperacionService.reestablecerContrasena(anyString(), anyString(), anyString()))
                .thenReturn((ResponseEntity) entity);

        mockMvc.perform(post("/recuperacion/reestablecerContra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Las contraseñas no coinciden"));
    }
}
