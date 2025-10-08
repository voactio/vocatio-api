package com.vocatio.exception;

import com.vocatio.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResultsException.class)
    public ResponseEntity<ErrorResponse> handleNoResults(NoResultsException ex) {
        // 428 Precondition Required según tu especificación
        return ResponseEntity.status(428)
                .body(new ErrorResponse("NO_RESULTS", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }
}