package com.vocatio.controller;

import com.vocatio.dto.CarreraResponseDTO;
import com.vocatio.service.CarreraService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carreras")
public class CarreraController {

    private final CarreraService carreraService;

    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    @GetMapping
    public Page<CarreraResponseDTO> listar(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "duracion", required = false) Integer duracion,
            @RequestParam(name = "modalidad", required = false) String modalidad
    ) {
        return carreraService.obtenerListadoPaginado(page, limit, duracion, modalidad);
    }
}

