package com.vocatio.controller;

import com.vocatio.dto.request.SubmitAnswerRequestDTO;
import com.vocatio.dto.response.PreguntaDTO;
import com.vocatio.dto.response.ResultadoTestDTO;
import com.vocatio.dto.response.StartTestResponseDTO;
import com.vocatio.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tests")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping("/{testId}/iniciar")
    public ResponseEntity<StartTestResponseDTO> iniciarTest(@PathVariable Long testId, @RequestParam Long userId) {
        StartTestResponseDTO response = testService.iniciarTest(testId, userId);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/sessions/{sessionId}/results")
    public ResponseEntity<ResultadoTestDTO> getTestResults(@PathVariable Long sessionId) {
        ResultadoTestDTO resultados = testService.getTestResults(sessionId);
        return ResponseEntity.ok(resultados);
    }
}