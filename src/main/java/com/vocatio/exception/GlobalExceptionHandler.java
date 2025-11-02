// java
package com.vocatio.exception;

import com.vocatio.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResultsException.class)
    public ResponseEntity<ErrorResponse> handleNoResults(NoResultsException ex) {
        // 428 Precondition Required
        return ResponseEntity.status(428)
                .body(new ErrorResponse("NO_RESULTS", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // 404 - no encontrado
    @ExceptionHandler(ResourceNorFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNorFoundException ex){
        return ResponseEntity.status(404).body(Map.of(
                "error", "NOT_FOUND",
                "message", ex.getMessage()
        ));
    }
}
