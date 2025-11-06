package com.vocatio.controller;

import com.vocatio.dto.response.CarreraCardResponse;
import com.vocatio.dto.response.CarreraDetailResponse;
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

    // Listado inicial de fichas (nombre + breve descripción)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/listado")
    public ResponseEntity<List<CarreraCardResponse>> listadoInicial(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(carreraService.listarInicial(page, size));
    }

    // Detalle de carrera
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CarreraDetailResponse> detalle(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.obtenerDetalle(id));
    }

    // Listado con filtros (US15)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<CarreraCardResponse>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String modalidad,
            @RequestParam(required = false) String perfilRiasec,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "nombre,asc") String sort
    ) {
        return ResponseEntity.ok(carreraService.buscar(nombre, modalidad, perfilRiasec, page, size, sort));
    }
}
