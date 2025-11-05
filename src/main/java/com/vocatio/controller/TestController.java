package com.vocatio.controller;

import com.vocatio.dto.request.SubmitAnswerRequestDTO;
import com.vocatio.dto.response.PreguntaDTO;
import com.vocatio.dto.response.ResultadoTestDTO;
import com.vocatio.dto.response.StartTestResponseDTO;
import com.vocatio.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tests")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{testId}/iniciar")
    public ResponseEntity<StartTestResponseDTO> iniciarTest(@PathVariable Long testId, @RequestParam UUID userId) {
        StartTestResponseDTO response = testService.iniciarTest(testId, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/sessions/{sessionId}/answers")
    public ResponseEntity<PreguntaDTO> submitAnswer(
            @PathVariable Long sessionId,
            @RequestBody SubmitAnswerRequestDTO answerRequest) {

        PreguntaDTO nextQuestion = testService.submitAnswerAndGetNext(
                sessionId,
                answerRequest.getPreguntaId(),
                answerRequest.getOpcionId()
        );

        if (nextQuestion != null) {
            return ResponseEntity.ok(nextQuestion);
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/sessions/{sessionId}/results")
    public ResponseEntity<ResultadoTestDTO> getTestResults(@PathVariable Long sessionId) {
        ResultadoTestDTO resultados = testService.getTestResults(sessionId);
        return ResponseEntity.ok(resultados);
    }
}