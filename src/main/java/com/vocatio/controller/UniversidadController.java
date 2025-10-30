package com.vocatio.controller;

import com.vocatio.dto.response.PosiblesUniversidades;
import com.vocatio.dto.response.UniversidadesItemDTO;
import com.vocatio.service.UniversidadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/careers")
public class UniversidadController {

    private final UniversidadService service;

    public UniversidadController(UniversidadService service) {
        this.service = service;
    }

    @GetMapping("/{careerId}/universities")
    public PosiblesUniversidades listByCareer(
            @PathVariable Long careerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.getByCareer(careerId, page, size); // el service lanza 404 si corresponde
    }
}
