package com.vocatio.unit;

import com.vocatio.dto.request.LoginRequest;
import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.dto.response.AuthResponse;
import com.vocatio.model.Role;
import com.vocatio.model.RoleType;
import com.vocatio.model.Usuario;
import com.vocatio.repository.RoleRepository;
import com.vocatio.repository.UsuarioRepository;
import com.vocatio.security.JwtUtil;
import com.vocatio.service.AuthService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Pruebas unitarias")
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleType.ROLE_USER);

        usuario = new Usuario();
        usuario.setCorreo("test@vocatio.com");
        usuario.setNombre("Test User");
        usuario.setId(UUID.randomUUID());
    }

    // LOGIN - UNIT 1 - EXITOSO
    @Test
    @DisplayName("Login exitoso devuelve AuthResponse con token")
    void loginUsuarioValido(){
        LoginRequest request = new LoginRequest();
        request.setCorreo(usuario.getCorreo());
        request.setContrasena("test12345");

        when(usuarioRepository.findByCorreo(usuario.getCorreo())).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateToken(usuario.getCorreo(), usuario.getNombre(), usuario.getId()))
                .thenReturn("token-jwt");

        // Obtener respuesta
        AuthResponse authResponse = authService.login(request);

        // Validar respuesta
        assertNotNull(authResponse);
        assertEquals("test@vocatio.com", authResponse.correo());
        assertEquals("Test User", authResponse.nombre());
        assertEquals("token-jwt", authResponse.token());

        verify(usuarioRepository).findByCorreo("test@vocatio.com");
        verify(jwtUtil).generateToken(usuario.getCorreo(), usuario.getNombre(), usuario.getId());
    }

    // LOGIN - UNIT 2 - FALLIDO
    @Test
    @DisplayName("Login con usuario inexistente - lanzar excepción")
    void loginUsuarioNoEncontrado() {
        LoginRequest request = new LoginRequest();
        request.setCorreo("noexiste@vocatio.com");
        request.setContrasena("noexiste12345");

        when(usuarioRepository.findByCorreo(request.getCorreo())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
        verify(usuarioRepository).findByCorreo(request.getCorreo());
    }

    // REGISTER - UNIT 1 - EXITOSO
    @Test
    @DisplayName("Register exitoso devuelve AuthResponse con token")
    void register_UsuarioValido() {
        RegisterUsuarioRequest request = new RegisterUsuarioRequest();
        request.setCorreo("test@vocatio.com");
        request.setContrasena("password123");
        request.setNombre("Test User");
        request.setNivelEducativo("Universitario");

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(passwordEncoder.encode(request.getContrasena())).thenReturn("encodedPassword");

        Usuario savedUsuario = new Usuario();
        savedUsuario.setId(UUID.randomUUID());
        savedUsuario.setCorreo(request.getCorreo());
        savedUsuario.setNombre(request.getNombre());
        savedUsuario.setContrasena("encodedPassword");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUsuario);
        when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.of(userRole));

        // Mockear JwtUtil usando el UUID del Usuario
        when(jwtUtil.generateToken(savedUsuario.getCorreo(), savedUsuario.getNombre(), savedUsuario.getId()))
                .thenReturn("token-jwt");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("test@vocatio.com", response.correo());
        assertEquals("Test User", response.nombre());
        assertEquals("token-jwt", response.token());

        verify(usuarioRepository).existsByCorreo(request.getCorreo());
        verify(passwordEncoder).encode(request.getContrasena());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(jwtUtil).generateToken(savedUsuario.getCorreo(), savedUsuario.getNombre(), savedUsuario.getId());
    }

    // REGISTER - UNIT 2 - ERROR - EMAIL DIPLICADO
    @Test
    @DisplayName("Register con email duplicado - lanzar excepción")
    void register_EmailDuplicado() {
        // Arrange
        RegisterUsuarioRequest request = new RegisterUsuarioRequest();
        request.setCorreo("test@vocatio.com");
        request.setContrasena("password123");
        request.setNombre("Test User");
        request.setNivelEducativo("Universitario");

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(true);

        // Act & Assert: se espera RuntimeException
        assertThrows(RuntimeException.class, () -> authService.register(request));

        // Verificación: no se debe guardar ningún usuario
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // REGISTER - UNIT 3 - ERROR - CONTRASEÑA INCUMPLE REGLAS
    @Test
    @DisplayName("Register con contraseña inválida lanza excepción")
    void register_ContrasenaInvalida() {
        // Arrange
        RegisterUsuarioRequest request = new RegisterUsuarioRequest();
        request.setCorreo("test@vocatio.com");
        request.setContrasena("abc123"); // contraseña inválida: < 8 caracteres
        request.setNombre("Test User");
        request.setNivelEducativo("Universitario");

        when(usuarioRepository.existsByCorreo(request.getCorreo())).thenReturn(false);

        // Se espera que tu servicio valide la contraseña y lance RuntimeException
        assertThrows(RuntimeException.class, () -> authService.register(request));

        // Verificación: no se debe guardar ningún usuario
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
