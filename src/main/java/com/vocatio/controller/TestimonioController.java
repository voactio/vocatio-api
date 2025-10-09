package com.vocatio.controller;

import com.vocatio.dto.TestimonioResponseDTO;
import com.vocatio.service.TestimonioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/carreras")
public class TestimonioController {

    private final TestimonioService testimonioService;

    public TestimonioController(TestimonioService testimonioService) {
        this.testimonioService = testimonioService;
    }

    @GetMapping("/{carreraId}/testimonios")
    public ResponseEntity<List<TestimonioResponseDTO>> listarPorCarrera(@PathVariable("carreraId") Long carreraId) {
        List<TestimonioResponseDTO> resultado = testimonioService.obtenerTestimoniosPorCarrera(carreraId);
        return ResponseEntity.ok(resultado);
    }
}

