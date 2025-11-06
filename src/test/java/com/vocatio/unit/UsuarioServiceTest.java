package com.vocatio.unit;

import com.vocatio.dto.request.UpdateUsuarioRequest;
import com.vocatio.model.Usuario;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.UsuarioRepository;
import com.vocatio.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Pruebas unitarias")
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNombre("Test User");
        usuario.setCorreo("test@vocatio.com");
        usuario.setContrasena("$2a$10$encodedPassword"); // simulando bcrypt
    }

    // UPDATE USUARIO - UNIT 1 - EXITOSO
    @Test
    @DisplayName("Modificar perfil exitosamente")
    void modificarPerfil_Exitoso() {
        // Arrange
        UpdateUsuarioRequest request = new UpdateUsuarioRequest();
        request.setNombre("Usuario Modificado");
        request.setContrasena("Password123");
        request.setNivelEducativo("Universitario");

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Usuario actualizado = usuarioService.updateUsuario(usuario.getId(), request);

        // Assert
        assertNotNull(actualizado);
        assertEquals("Usuario Modificado", actualizado.getNombre());
        assertEquals("Universitario", actualizado.getNivelEducativo());
        assertNotNull(actualizado.getContrasena()); // se codifica
        assertTrue(actualizado.getActualizadoEn().isBefore(LocalDateTime.now().plusSeconds(1)));

        verify(usuarioRepository).findById(usuario.getId());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    // UPDATE USUARIO - UNIT 2 - CONTRASEÑA NO CUMPLE CON LOS REQUISITOS
    @Test
    @DisplayName("Modificar perfil con contraseña inválida lanza excepción")
    void modificarPerfil_ContrasenaInvalida() {
        // Arrange
        UpdateUsuarioRequest request = new UpdateUsuarioRequest();
        request.setNombre("Usuario Modificado");
        request.setContrasena("abc123"); // inválida
        request.setNivelEducativo("Universitario");

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.updateUsuario(usuario.getId(), request));

        assertEquals("Contraseña inválida: debe tener letras, números y al menos 8 caracteres", exception.getMessage());

        verify(usuarioRepository).findById(usuario.getId());
        verify(usuarioRepository, never()).save(any());
    }
}