// src/main/java/com/vocatio/controller/CarreraController.java
package com.vocatio.controller;

import com.vocatio.dto.response.CarreraDetailResponse;
import com.vocatio.service.CarreraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
