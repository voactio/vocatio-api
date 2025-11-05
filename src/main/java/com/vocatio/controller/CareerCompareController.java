package com.vocatio.controller;

import com.vocatio.dto.request.CompareCareersRequest;
import com.vocatio.dto.response.CompareCareersResponse;
import com.vocatio.service.CareerCompareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carreras")
@RequiredArgsConstructor
public class CareerCompareController {

    private final CareerCompareService careerCompareService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/comparar")
    public ResponseEntity<CompareCareersResponse> compararCarreras(
            @RequestBody CompareCareersRequest request
    ) {
        CompareCareersResponse response = careerCompareService.comparar(request);
        return ResponseEntity.ok(response);
    }
}
