package com.vocatio.unit;

import com.vocatio.model.TokenRecuperacion;
import com.vocatio.model.Usuario;
import com.vocatio.repository.TokenRecuperacionRepository;
import com.vocatio.repository.UsuarioRepository;
import com.vocatio.service.RecuperacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecuperacionService - Pruebas unitarias")
public class RecuperacionServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRecuperacionRepository tokenRecuperacionRepository;

    @InjectMocks
    private RecuperacionService recuperacionService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setCorreo("test@vocatio.com");
        usuario.setNombre("Test User");
    }

    // RECUPERACION - Olvidó contraseña - UNIT 1 - EXITOSO
    @Test
    @DisplayName("Olvido de contraseña - correo existe - éxito")
    void olvidoContrasena_CorreoExiste_Exito() {
        String correo = "test@vocatio.com";

        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.of(usuario));
        when(tokenRecuperacionRepository.save(any(TokenRecuperacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = recuperacionService.olvidoContrasena(correo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Se envió un enlace de recuperación al correo registrado", body.get("mensaje"));

        verify(usuarioRepository).findByCorreo(correo);
        verify(tokenRecuperacionRepository).save(any(TokenRecuperacion.class));
    }

    // RECUPERACION - Olvidó contraseña - UNIT 2 - Correo no existe
    @Test
    @DisplayName("Olvido de contraseña - correo no existe - error 404")
    void olvidoContrasena_CorreoNoExiste_Error() {
        String correo = "noexiste@vocatio.com";

        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.empty());

        ResponseEntity<?> response = recuperacionService.olvidoContrasena(correo);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("No existe una cuenta con el correo ingresado", body.get("mensaje"));

        verify(usuarioRepository).findByCorreo(correo);
        verify(tokenRecuperacionRepository, never()).save(any(TokenRecuperacion.class));
    }

    // RECUPERACION - Exite token de recuperacion - UNIT 3 - EXITOSO
    @Test
    @DisplayName("Validar token - token válido - éxito")
    void validarToken_TokenValido_Exito() {
        String token = "token-existente";

        TokenRecuperacion tokenRecuperacion = new TokenRecuperacion();
        tokenRecuperacion.setToken(token);
        tokenRecuperacion.setExpiracion(LocalDateTime.now().plusMinutes(30)); // aún válido

        when(tokenRecuperacionRepository.findByToken(token)).thenReturn(Optional.of(tokenRecuperacion));

        ResponseEntity<?> response = recuperacionService.validarToken(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Token válido. Puede establecer una nueva contraseña", body.get("mensaje"));

        verify(tokenRecuperacionRepository).deleteAllByExpiracionBefore(any());
        verify(tokenRecuperacionRepository).findByToken(token);
    }

    // RECUPERACION - NO exite token de recuperacion - UNIT 4 - Error
    @Test
    @DisplayName("Validar token - token no existe - error")
    void validarToken_TokenNoExiste_Error() {
        String token = "token-inexistente";

        when(tokenRecuperacionRepository.findByToken(token)).thenReturn(Optional.empty());

        ResponseEntity<?> response = recuperacionService.validarToken(token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Token invalido", body.get("mensaje"));

        verify(tokenRecuperacionRepository).deleteAllByExpiracionBefore(any());
        verify(tokenRecuperacionRepository).findByToken(token);
    }

    // RECUPERACION - Reestablecer contraseña - UNIT 5 - EXITOSO
    @Test
    @DisplayName("Reestablecer contraseña exitoso")
    void reestablecerContrasena_Exitoso() {
        String token = "token-valido";
        String nuevaContrasena = "Password123";
        String confirmarContrasena = "Password123";

        // Mockear usuario y token
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setCorreo("test@vocatio.com");

        TokenRecuperacion tokenRecuperacion = new TokenRecuperacion();
        tokenRecuperacion.setToken(token);
        tokenRecuperacion.setExpiracion(LocalDateTime.now().plusMinutes(30));
        tokenRecuperacion.setUsuario(usuario);

        when(tokenRecuperacionRepository.findByToken(token)).thenReturn(Optional.of(tokenRecuperacion));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = recuperacionService.reestablecerContrasena(token, nuevaContrasena, confirmarContrasena);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("La contraseña se ha restablecido correctamente", body.get("mensaje"));

        verify(usuarioRepository).save(usuario);
        verify(tokenRecuperacionRepository).delete(tokenRecuperacion);
    }

    // RECUPERACION - Reestablecer contraseña - UNIT 6 - Contraseñas ingresadas no coinciden
    @Test
    @DisplayName("Reestablecer contraseña - contraseñas no coinciden")
    void reestablecerContrasena_ContrasenasNoCoinciden() {
        String token = "token-valido";
        String nuevaContrasena = "Password123";
        String confirmarContrasena = "Password456";

        // Mockear usuario y token
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());

        TokenRecuperacion tokenRecuperacion = new TokenRecuperacion();
        tokenRecuperacion.setToken(token);
        tokenRecuperacion.setExpiracion(LocalDateTime.now().plusMinutes(30));
        tokenRecuperacion.setUsuario(usuario);

        when(tokenRecuperacionRepository.findByToken(token)).thenReturn(Optional.of(tokenRecuperacion));

        ResponseEntity<?> response = recuperacionService.reestablecerContrasena(token, nuevaContrasena, confirmarContrasena);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Las contraseñas no coinciden", body.get("mensaje"));

        verify(usuarioRepository, never()).save(any());
        verify(tokenRecuperacionRepository, never()).delete(any());
    }
}
