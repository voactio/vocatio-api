package com.vocatio.service;

import com.vocatio.model.TokenRecuperacion;
import com.vocatio.model.Usuario;
import com.vocatio.repository.TokenRecuperacionRepository;
import com.vocatio.repository.UsuarioRepository;
import org.antlr.v4.runtime.Token;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// FUNCIONALIDAD 4 - RECUPERACION DE CONTRASEÑA
@Service
public class RecuperacionService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRecuperacionRepository;
    public RecuperacionService(UsuarioRepository usuarioRepository, TokenRecuperacionRepository tokenRecuperacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRecuperacionRepository = tokenRecuperacionRepository;
    }

    // Recuperar Contraseña - INGRESAR CORREO ELECTRONICO
    public ResponseEntity<?> olvidoContrasena(String correo){
        if(correo==null || correo.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("mensaje","Correo no puede estar vacío"));

        if (!correo.contains("@"))
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Correo no valido"));

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje","No existe una cuenta con el correo ingresado"));

        Usuario usuario = usuarioOpt.get();

        // Generar token y guardarlo
        String token = UUID.randomUUID().toString();
        TokenRecuperacion tokenRecuperacion = new TokenRecuperacion();
        tokenRecuperacion.setToken(token);
        tokenRecuperacion.setUsuario(usuario);
        tokenRecuperacion.setExpiracion(LocalDateTime.now().plusMinutes(30));
        tokenRecuperacionRepository.save(tokenRecuperacion);

        // SIMULACION DE ENVIO
        System.out.println("Enlace de recuperación: http://localhost:8080/api/auth/reset-password?token=" + token);
        return ResponseEntity.ok(Map.of("mensaje", "Se envió un enlace de recuperación al correo registrado"));
    }

    // Recuperar Contraseña - VALIDEZ DEL TOKEN INGRESADO
    public ResponseEntity<?> validarToken(String token){
        // borrar tokens vencidos
        tokenRecuperacionRepository.deleteAllByExpiracionBefore(LocalDateTime.now());

        Optional<TokenRecuperacion> tokenOpt = tokenRecuperacionRepository.findByToken(token);

        if (tokenOpt.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("mensaje","Token invalido"));

        TokenRecuperacion tokenRecuperacion = tokenOpt.get();

        if(tokenRecuperacion.getExpiracion().isBefore(LocalDateTime.now()))
            return ResponseEntity.badRequest().body(Map.of("mensaje","El token ha expirado"));

        return ResponseEntity.ok(Map.of("mensaje", "Token válido. Puede establecer una nueva contraseña"));
    }

    // Recuperar Contraseña - INGRESAR Y CONFIRMAR NUEVA CONTRASENA
    public ResponseEntity<?> reestablecerContrasena(String token, String nuevaContrasena, String confirmarContrasena){
        Optional<TokenRecuperacion> tokenOpt = tokenRecuperacionRepository.findByToken(token);
        if (tokenOpt.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("mensaje","Token invalido"));

        TokenRecuperacion tokenRecuperacion = tokenOpt.get();

        if (tokenRecuperacion.getExpiracion().isBefore(LocalDateTime.now()))
            return ResponseEntity.badRequest().body(Map.of("mensaje","Token ha expirado"));

        if (!nuevaContrasena.equals(confirmarContrasena))
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Las contraseñas no coinciden"));

        if (nuevaContrasena.length() < 8 || !nuevaContrasena.matches("^(?=.*[A-Za-z])(?=.*\\d).+$"))
            return ResponseEntity.unprocessableEntity().body(Map.of("mensaje", "La contraseña debe tener al menos 8 caracteres e incluir letras y números"));

        Usuario user =  tokenRecuperacion.getUsuario();
        user.setContrasena(BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt()));
        usuarioRepository.save(user);

        tokenRecuperacionRepository.delete(tokenRecuperacion);

        return ResponseEntity.ok(Map.of("mensaje", "La contraseña se ha restablecido correctamente"));
    }
}
