package com.vocatio.service;

import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.dto.request.UpdateUsuarioRequest;
import com.vocatio.model.Usuario;
import com.vocatio.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
        user.setCarreraActual(request.getCarreraActual());
        user.setUrlImagenPerfil(request.getUrlImagenPerfil());

        return usuarioRepository.save(user);
    }

    // FUNCIONALIDAD 2 - MODIFICAR PERFIL
    public Usuario updateUsuario(Long id, UpdateUsuarioRequest request) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario user = optionalUsuario.get();

        // actualizar perfil
        user.setNombre(request.getNombre());
        user.setContrasena(BCrypt.hashpw(request.getContrasena(), BCrypt.gensalt()));
        user.setNivelEducativo(request.getNivelEducativo());

        if (request.getCarreraActual() != null) {
            user.setCarreraActual(request.getCarreraActual());
        }
        if (request.getUrlImagenPerfil() != null) {
            user.setUrlImagenPerfil(request.getUrlImagenPerfil());
        }

        user.setActualizadoEn(LocalDateTime.now());

        return usuarioRepository.save(user);
    }
}

