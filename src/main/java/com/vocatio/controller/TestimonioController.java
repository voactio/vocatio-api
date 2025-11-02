// src/main/java/com/vocatio/controller/TestimonioController.java
package com.vocatio.controller;

import com.vocatio.dto.response.TestimonioResponse;
import com.vocatio.service.TestimonioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carreras/{carreraId}/testimonios")
public class TestimonioController {

    private final TestimonioService testimonioService;

    public TestimonioController(TestimonioService testimonioService) {
        this.testimonioService = testimonioService;
    }

    @GetMapping
    public ResponseEntity<List<TestimonioResponse>> listar(@PathVariable Long carreraId) {
        return ResponseEntity.ok(testimonioService.obtenerAprobadosPorCarrera(carreraId));
    }
}
