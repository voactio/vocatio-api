package com.vocatio.controller;

import com.vocatio.dto.response.RecursoResponse;
import com.vocatio.service.RecursosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carreras")
@RequiredArgsConstructor
public class RecursosController {

    private final RecursosService recursosService;

    @GetMapping("/{idCarrera}/recursos")
    public ResponseEntity<List<RecursoResponse>> listarRecursosPorCarrera(
            @PathVariable Long idCarrera
    ) {
        List<RecursoResponse> recursos = recursosService.listarPorCarrera(idCarrera);
        return ResponseEntity.ok(recursos);
    }
}
