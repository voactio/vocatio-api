package com.vocatio.controller;

import com.vocatio.service.RecuperacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/recuperacion")
public class RecuperacionController {

    private final RecuperacionService recuperacionService;

    public RecuperacionController(RecuperacionService recuperacionService) {
        this.recuperacionService = recuperacionService;
    }

    @PostMapping("/olvidoContra")
    public ResponseEntity<?> olvidar(@RequestBody Map<String, String> body) {
        return recuperacionService.olvidoContrasena(body.get("correo"));
    }

    @GetMapping("/reestablecerContra")
    public ResponseEntity<?> validar(@RequestParam String token) {
        return recuperacionService.validarToken(token);
    }

    @PostMapping("/reestablecerContra")
    public ResponseEntity<?> reestablecer(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String nuevaContra = body.get("nuevaContrasena");
        String confirmarContra = body.get("confirmarContrasena");
        return recuperacionService.reestablecerContrasena(token, nuevaContra, confirmarContra);
    }
}
