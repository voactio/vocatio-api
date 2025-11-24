package com.vocatio.controller;

import com.vocatio.dto.request.OlvidoRequest;
import com.vocatio.dto.request.ReContrasenaRequest;
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
    public ResponseEntity<?> olvidar(@RequestBody OlvidoRequest olvidoRequest) {
        return recuperacionService.olvidoContrasena(olvidoRequest.getCorreo());
    }

    @GetMapping("/reestablecerContra")
    public ResponseEntity<?> validar(@RequestParam String token) {
        return recuperacionService.validarToken(token);
    }

    @PostMapping("/reestablecerContra")
    public ResponseEntity<?> reestablecer(@RequestBody ReContrasenaRequest reContrasenaRequest) {
        String token = reContrasenaRequest.getToken();
        String nuevaContra = reContrasenaRequest.getNuevaContrasena();
        String confirmarContra = reContrasenaRequest.getConfirmarContrasena();
        return recuperacionService.reestablecerContrasena(token, nuevaContra, confirmarContra);
    }
}
