package com.vocatio.controller;

import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.model.Usuario;
import com.vocatio.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUsuarioRequest request) {
        Usuario user = usuarioService.register(request);
        user.setContrasena(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}

