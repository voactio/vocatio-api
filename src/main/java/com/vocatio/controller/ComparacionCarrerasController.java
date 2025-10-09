package com.vocatio.controller;

import com.vocatio.dto.request.CompareCareersRequest;
import com.vocatio.dto.response.CompareCareersResponse;
import com.vocatio.service.CareerCompareService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carreras")
public class ComparacionCarrerasController {

    private final CareerCompareService compareService;

    public ComparacionCarrerasController(CareerCompareService compareService) {
        this.compareService = compareService;
    }

    @GetMapping("/compare")
    public ResponseEntity<CompareCareersResponse> compare(@RequestParam("ids") List<Long> ids) {
        if (ids == null || ids.size() != 2) {
            throw new IllegalArgumentException("Deben venir exactamente 2 IDs de carrera");
        }
        if (ids.get(0).equals(ids.get(1))) {
            throw new IllegalArgumentException("Los IDs deben ser distintos");
        }
        CompareCareersResponse resp = compareService.compare(ids.get(0), ids.get(1));
        return ResponseEntity.ok(resp);
    }
    private Long parseId(String s) {
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID inválido: " + s);
        }
    }
}
