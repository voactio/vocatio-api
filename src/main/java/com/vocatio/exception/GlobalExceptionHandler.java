package com.vocatio.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
    // 400 - reglas de negocio (IDs repetidos, cantidad != 2, ID no numérico, etc.)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(Map.of(
                "error", "BAD_REQUEST",
                "message", ex.getMessage()
        ));
    }

    // 404 - no encontrado (carrera inexistente)
    @ExceptionHandler(ResourceNorFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNorFoundException ex){
        return ResponseEntity.status(404).body(Map.of(
                "error", "NOT_FOUND",
                "message", ex.getMessage()
        ));
    }
}
