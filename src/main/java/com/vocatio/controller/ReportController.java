package com.vocatio.controller;

import com.vocatio.model.Report;
import com.vocatio.service.ReportService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users/{userId}/reports")
public class ReportController {

    private final ReportService reportService;
    public ReportController(ReportService reportService){ this.reportService = reportService; }

    @PostMapping
    public ResponseEntity<?> create(@PathVariable UUID userId,
                                    @RequestBody Map<String,String> body) {
        try {
            UUID testId = body.get("testId") != null ? UUID.fromString(body.get("testId")) : null;
            Report r = reportService.generatePdf(userId, testId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "reportId", r.getId(),
                    "status", r.getStatus().name(),
                    "downloadUrl", "/api/v1/users/" + userId + "/reports/latest",
                    "createdAt", r.getCreatedAt().toString(),
                    "summary", Map.of(
                            "lastTestId", r.getTestId()
                    )
            ));
        } catch (IllegalStateException ise) {
            if ("NO_RESULTS".equals(ise.getMessage())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "NO_RESULTS",
                        "message", "Aún no tienes resultados para descargar."
                ));
            }
            throw ise;
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<?> latest(@PathVariable UUID userId) throws Exception {
        try {
            File f = reportService.getLatestReportFile(userId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", f.getName());
            return new ResponseEntity<>(Files.readAllBytes(f.toPath()), headers, HttpStatus.OK);
        } catch (IllegalStateException ise) {
            if ("NO_RESULT".equals(ise.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "error", "NO_RESULT",
                        "message", "Aún no tienes resultados para descargar."
                ));
            }
            throw ise;
        }
    }
}
