package com.vocatio.service;

import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.model.Usuario;
import com.vocatio.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

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
}

