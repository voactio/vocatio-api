package com.vocatio.controller;

import com.vocatio.dto.CarreraDetailDTO;
import com.vocatio.dto.CarreraResponseDTO;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.service.CarreraService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carreras")
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

    @GetMapping
    public ResponseEntity<Page<CarreraResponseDTO>> listar(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "duracion", required = false) Integer duracion,
            @RequestParam(name = "modalidad", required = false) String modalidad
    ) {
        return ResponseEntity.ok(carreraService.obtenerListadoPaginado(page, limit, duracion, modalidad));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}