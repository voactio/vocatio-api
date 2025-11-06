package com.vocatio.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocatio.controller.AuthController;
import com.vocatio.dto.request.LoginRequest;
import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.dto.response.AuthResponse;
import com.vocatio.model.Usuario;
import com.vocatio.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - Pruebas funcionales")
public class AuthControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterUsuarioRequest registerRequest;
    private LoginRequest loginRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Inicializar MockMvc con el controlador y mocks inyectados
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        // Datos de prueba - register
        registerRequest = new RegisterUsuarioRequest();
        registerRequest.setCorreo("test@vocatio.com");
        registerRequest.setContrasena("Password123");
        registerRequest.setNombre("Test User");
        registerRequest.setNivelEducativo("Universitario");

        // Datos de prueba - login
        loginRequest = new LoginRequest();
        loginRequest.setCorreo("test@vocatio.com");
        loginRequest.setContrasena("Password123");

        // Usuario de prueba
        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setCorreo(registerRequest.getCorreo());
        usuario.setNombre(registerRequest.getNombre());
    }

    // Register - CONT 1 - Usuario registrado exitosamente
    @Test
    @DisplayName("POST /auth/register - registro exitoso")
    void registerUsuarioExitoso() throws Exception {
        AuthResponse response = new AuthResponse("token-jwt", usuario.getCorreo(), usuario.getNombre());
        when(authService.register(any(RegisterUsuarioRequest.class))).thenReturn(response);

        // ejecuta el POST
        // espera que los datos del usuario ingresado sean iguales al que retorna
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value(usuario.getCorreo()))
                .andExpect(jsonPath("$.nombre").value(usuario.getNombre()))
                .andExpect(jsonPath("$.token").value("token-jwt"));
    }

    // Register - CONT 2 - Error en el registro - correo existe
    @Test
    @DisplayName("POST /auth/register - correo duplicado")
    void registerCorreoDuplicado() throws Exception {
        when(authService.register(any(RegisterUsuarioRequest.class))).thenThrow(new RuntimeException("El correo ya está registrado"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error en el registro"))
                .andExpect(jsonPath("$.detalle").value("El correo ya está registrado"));
    }

    // Login - CONT 3 - Login exitoso
    @Test
    @DisplayName("POST /auth/login - login exitoso")
    void loginExitoso() throws Exception {
        AuthResponse response = new AuthResponse("token-jwt", usuario.getCorreo(), usuario.getNombre());
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value(usuario.getCorreo()))
                .andExpect(jsonPath("$.nombre").value(usuario.getNombre()))
                .andExpect(jsonPath("$.token").value("token-jwt"));
    }

    // Login - CONT 4 - Login no procede - Correo/Usuario no existe
    @Test
    @DisplayName("POST /auth/login - usuario no encontrado")
    void loginUsuarioNoExiste() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Error de autenticación"))
                .andExpect(jsonPath("$.detalle").value("Usuario no encontrado"));
    }
}
