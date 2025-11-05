package com.vocatio.controller;

import com.vocatio.dto.request.LoginRequest;
import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.dto.response.AuthResponse;
import com.vocatio.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // FUNCIONALIDAD 1 - REGISTRAR NUEVO USUARIO
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUsuarioRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Error en el registro", e.getMessage()));
        }
    }

    // FUNCIONALIDAD 3 - INICIAR SESION (LOGIN)
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessage("Error de autenticación", e.getMessage()));
        }
    }

    // Clase interna para respuesta de error simple
    private record ErrorMessage(String error, String detalle) {}
}