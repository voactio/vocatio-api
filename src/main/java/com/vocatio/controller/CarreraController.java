package com.vocatio.controller;

import com.vocatio.dto.CarreraDetailDTO;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.service.CarreraService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<CarreraDetailDTO> obtenerDetalle(@PathVariable("id") Long id) {
        CarreraDetailDTO detalle = carreraService.obtenerDetalleCarrera(id);
        return ResponseEntity.ok(detalle);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
