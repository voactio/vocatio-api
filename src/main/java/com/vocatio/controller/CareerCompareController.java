package com.vocatio.controller;

import com.vocatio.dto.request.CompareCareersRequest;
import com.vocatio.dto.response.CompareCareersResponse;
import com.vocatio.service.CareerCompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comparar-carreras")
public class CareerCompareController {

    @Autowired
    private CareerCompareService careerCompareService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> compararCarreras(@RequestBody CompareCareersRequest request) {
        System.out.println("===== INICIANDO COMPARACIÓN DE CARRERAS =====");
        System.out.println("Request recibido: " + request);

        try {
            CompareCareersResponse response = careerCompareService.comparar(request);
            System.out.println("Comparación exitosa");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error al comparar: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("message", "Error al comparar carreras: " + e.getMessage());
            errorBody.put("status", 400);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        }
    }
}