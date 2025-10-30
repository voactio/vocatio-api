package com.vocatio.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocatio.model.Report;
import com.vocatio.model.ResultadosTest;
import com.vocatio.repository.ReportRepository;
import com.vocatio.repository.ResultadosTestRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ResultadosTestRepository resultadosTestRepository;
    private final ObjectMapper objectMapper;

    public ReportService(ReportRepository reportRepository,
                         ResultadosTestRepository resultadosTestRepository,
                         ObjectMapper objectMapper) {
        this.reportRepository = reportRepository;
        this.resultadosTestRepository = resultadosTestRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Report generatePdf(UUID userId, UUID testId) {
        // 1) Validar que el usuario tenga al menos un resultado
        Optional<ResultadosTest> last = resultadosTestRepository
                .findFirstByIdUsuarioOrderByCompletadoEnDesc(userId);
        if (last.isEmpty()) throw new IllegalStateException("NO_RESULTS");

        try {
            // 2) Elegir el resultado objetivo: si viene testId úsalo, si no el último
            Optional<ResultadosTest> target = (testId != null)
                    ? resultadosTestRepository.findByIdTest(testId)
                    : last;

            if (target.isEmpty()) throw new IllegalStateException("NO_RESULTS");

            // 3) Parsear puntajes JSONB (String) -> Map<String, Number>
            Map<String, Number> puntajes = null;
            String raw = target.get().getPuntajes(); // asegúrate que tu entidad expone String
            if (raw != null && !raw.isBlank()) {
                puntajes = objectMapper.readValue(raw, new TypeReference<Map<String, Number>>() {});
            }

            // 4) Generar PDF válido
            Path outDir = Path.of("generated-reports");
            String fileName = "rpt-" + UUID.randomUUID() + ".pdf";
            Path pdfPath = createPdfReportWithPdfBox(outDir, fileName, userId, testId, puntajes);

            // 5) Persistir metadata del reporte
            Report r = new Report();
            r.setUserId(userId);
            r.setTestId(testId);
            r.setFilePath(pdfPath.toAbsolutePath().toString());
            r.setStatus(Report.Status.READY);
            r.setCreatedAt(OffsetDateTime.now());
            return reportRepository.save(r);

        } catch (IllegalStateException e) {
            throw e; // "NO_RESULTS" lo mapea tu controller a 428/400 según tu regla
        } catch (Exception e) {
            throw new RuntimeException("FAIL_TO_GENERATE_PDF", e);
        }
    }

    @Transactional(readOnly = true)
    public File getLatestReportFile(UUID userId) {
        Report rpt = reportRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new IllegalStateException("NO_RESULT"));
        return new File(rpt.getFilePath());
    }

    // ---------- Helpers ----------

    private Path createPdfReportWithPdfBox(Path outputDir,
                                           String fileName,
                                           UUID userId,
                                           UUID testId,
                                           Map<String, Number> puntajes) throws Exception {
        Files.createDirectories(outputDir);
        Path pdfPath = outputDir.resolve(fileName);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 50f;
                float y = page.getMediaBox().getHeight() - margin;

                // Título
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
                cs.newLineAtOffset(margin, y);
                cs.showText("Reporte de Resultados — Vocatio");
                cs.endText();
                y -= 28f;

                // Metadatos
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText("Usuario: " + userId);
                cs.endText();
                y -= 16f;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText("Test ID: " + (testId != null ? testId : "—"));
                cs.endText();
                y -= 20f;

                // Línea divisoria
                cs.moveTo(margin, y);
                cs.lineTo(page.getMediaBox().getWidth() - margin, y);
                cs.stroke();
                y -= 18f;

                // Puntajes
                if (puntajes != null && !puntajes.isEmpty()) {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    cs.newLineAtOffset(margin, y);
                    cs.showText("Resultados por dimensión");
                    cs.endText();
                    y -= 16f;

                    for (var e : puntajes.entrySet()) {
                        if (y < margin + 40f) break; // evita desbordar la página
                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA, 12);
                        cs.newLineAtOffset(margin, y);
                        cs.showText(e.getKey() + ": " + e.getValue());
                        cs.endText();
                        y -= 14f;
                    }
                } else {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
                    cs.newLineAtOffset(margin, y);
                    cs.showText("Aún no hay puntajes para mostrar.");
                    cs.endText();
                }
            }

            doc.save(pdfPath.toFile());
        }

        return pdfPath;
    }
}
