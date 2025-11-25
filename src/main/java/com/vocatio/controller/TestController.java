package com.vocatio.controller;

import com.vocatio.dto.request.SubmitAnswerRequestDTO;
import com.vocatio.dto.response.PreguntaDTO;
import com.vocatio.dto.response.ResultadoTestDTO;
import com.vocatio.dto.response.StartTestResponseDTO;
import com.vocatio.dto.response.TestHistoryResponse;
import com.vocatio.security.JwtUtil;
import com.vocatio.service.TestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;
    private final JwtUtil jwtUtil;

    private UUID getUserIdFromToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("Usuario no autenticado");
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{testId}/iniciar")
    public ResponseEntity<StartTestResponseDTO> iniciarTest(@PathVariable Long testId, HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        StartTestResponseDTO response = testService.iniciarTest(testId, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/sessions/{sessionId}/answers")
    public ResponseEntity<PreguntaDTO> submitAnswer(
            @PathVariable Long sessionId,
            @RequestBody SubmitAnswerRequestDTO answerRequest) {

        PreguntaDTO nextQuestion = testService.submitAnswerAndGetNext(sessionId, answerRequest);

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

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/historial")
    public ResponseEntity<List<TestHistoryResponse>> getHistorial(HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        return ResponseEntity.ok(testService.getHistorial(userId));
    }
}