package com.vocatio.service;

import com.vocatio.dto.request.LoginRequest;
import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.dto.request.UpdateUsuarioRequest;
import com.vocatio.model.Usuario;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CarreraRepository carreraRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, CarreraRepository carreraRepository) {
        this.usuarioRepository = usuarioRepository;
        this.carreraRepository = carreraRepository;
    }

    // FUNCIONALIDAD 1 - REGISTRARSE
    public Usuario register(RegisterUsuarioRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está en uso");
        }

        Usuario user = new Usuario();
        user.setNombre(request.getNombre());
        user.setCorreo(request.getCorreo());
        user.setContrasena(BCrypt.hashpw(request.getContrasena(), BCrypt.gensalt()));
        user.setNivelEducativo(request.getNivelEducativo());
        user.setUrlImagenPerfil(request.getUrlImagenPerfil());
        if(request.getCarreraId() != null){
            user.setCarrera(
                    carreraRepository.findById(request.getCarreraId())
                            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada"))
            );
        }

        return usuarioRepository.save(user);
    }

    // FUNCIONALIDAD 2 - MODIFICAR PERFIL
    public Usuario updateUsuario(UUID id, UpdateUsuarioRequest request) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario user = optionalUsuario.get();

        // actualizar perfil
        user.setNombre(request.getNombre());
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            String password = request.getContrasena();
            if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
                throw new RuntimeException("Contraseña inválida: debe tener letras, números y al menos 8 caracteres");
            }
            user.setContrasena(BCrypt.hashpw(request.getContrasena(), BCrypt.gensalt()));
        }
        user.setNivelEducativo(request.getNivelEducativo());

        if (request.getCarreraId() != null) {
            user.setCarrera(
                    carreraRepository.findById(request.getCarreraId())
                            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada"))
            );
        }
        if (request.getUrlImagenPerfil() != null) {
            user.setUrlImagenPerfil(request.getUrlImagenPerfil());
        }

        user.setActualizadoEn(LocalDateTime.now());

        return usuarioRepository.save(user);
    }

    // FUNCIONALIDAD 3 - INICIAR SESION
    public Map<String, Object> login(LoginRequest request) {
        if (request.getCorreo() == null || request.getCorreo().isEmpty())
            throw new IllegalArgumentException("El correo no puede estar vacío");

        if (request.getContrasena() == null || request.getContrasena().isEmpty())
            throw new IllegalArgumentException("La contraseña no puede estar vacía");

        if (!request.getCorreo().contains("@"))
            throw new IllegalArgumentException("Ingresa un correo válido");

        Usuario user = usuarioRepository.findByCorreo(request.getCorreo()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!BCrypt.checkpw(request.getContrasena(), user.getContrasena()))
            throw new RuntimeException("El correo y/o contraseña no son válidos");

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Inicio de sesión exitoso");
        response.put("usuario", Map.of(
                "id", user.getId(),
                "nombre", user.getNombre(),
                "correo", user.getCorreo(),
                "nivelEducativo", user.getNivelEducativo(),
                "carreraId", user.getCarrera().getId()
        ));

        return response;
    }
}

