package com.vocatio.controller;

import com.vocatio.dto.response.CarreraDetailResponse;
import com.vocatio.service.CarreraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carreras")
public class CarreraController {

    private final CarreraService carreraService;

    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarreraDetailResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.obtenerDetalleCarrera(id));
    }

    // Listado con filtros opcionales: ?area=Ingenieria&duracion=5&modalidad=Presencial
    @GetMapping
    public ResponseEntity<List<CarreraDetailResponse>> filtrar(
            @RequestParam(required = false, name = "area") String area,
            @RequestParam(required = false, name = "duracion") Integer duracion,
            @RequestParam(required = false, name = "modalidad") String modalidad
    ) {
        return ResponseEntity.ok(carreraService.filtrar(area, duracion, modalidad));
    }
}
