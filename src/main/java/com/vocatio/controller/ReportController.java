package com.vocatio.controller;

import com.vocatio.dto.response.GenerateReportResponse;
import com.vocatio.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/resultados")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService resultadoService;

    // para ver el detalle y el ranking
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}/detalle")
    public GenerateReportResponse obtenerDetalle(@PathVariable("id") Long idResultado,
                                                     @RequestParam("usuario") UUID idUsuario) {
        return resultadoService.obtenerResultadoConRecomendaciones(idResultado, idUsuario);
    }

    // para descargar el PDF
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable("id") Long idResultado,
                                               @RequestParam("usuario") UUID idUsuario) {
        byte[] pdf = resultadoService.generarPdfResultado(idResultado, idUsuario);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=resultado-" + idResultado + ".pdf")
                .body(pdf);
    }
}
