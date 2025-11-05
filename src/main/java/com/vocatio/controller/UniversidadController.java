package com.vocatio.controller;

import com.vocatio.dto.request.UniversitiesByCareerRequest;
import com.vocatio.dto.response.UniversitiesByCareerResponse;
import com.vocatio.service.UniversidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carreras")
@RequiredArgsConstructor
public class UniversidadController {

    private final UniversidadService service;

    @PostMapping("/{idCarrera}/universidades")
    public ResponseEntity<UniversitiesByCareerResponse> listarUniversidades(
            @PathVariable Long idCarrera
    ) {
        var req = new UniversitiesByCareerRequest();
        req.setIdCarrera(idCarrera);

        var resp = service.obtener(req);
        return ResponseEntity.ok(resp);
    }
}
