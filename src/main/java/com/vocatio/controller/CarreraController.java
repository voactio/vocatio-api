package com.vocatio.controller;

import com.vocatio.dto.response.CarreraCardResponse;
import com.vocatio.service.CarreraService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carreras")
public class CarreraController {

    private final CarreraService carreraService;

    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    // ... endpoints existentes (detalle, filtros, etc.)

    // Listado inicial de fichas (nombre + breve descripción)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/listado")
    public ResponseEntity<List<CarreraCardResponse>> listadoInicial(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(carreraService.listarInicial(page, size));
    }
}
