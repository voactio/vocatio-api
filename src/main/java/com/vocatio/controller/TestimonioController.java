// src/main/java/com/vocatio/controller/TestimonioController.java
package com.vocatio.controller;

import com.vocatio.dto.request.CrearTestimonioRequest;
import com.vocatio.dto.response.TestimonioResponse;
import com.vocatio.service.TestimonioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/carreras/{carreraId}/testimonios")
public class TestimonioController {

    private final TestimonioService testimonioService;

    public TestimonioController(TestimonioService testimonioService) {
        this.testimonioService = testimonioService;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<TestimonioResponse>> listar(@PathVariable Long carreraId) {
        return ResponseEntity.ok(testimonioService.obtenerAprobadosPorCarrera(carreraId));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<TestimonioResponse> crear(
            @PathVariable Long carreraId,
            @RequestBody CrearTestimonioRequest request
    ) {
        TestimonioResponse nuevoTestimonio = testimonioService.crearTestimonio(carreraId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTestimonio);
    }
}