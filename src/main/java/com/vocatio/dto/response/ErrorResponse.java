package com.vocatio.dto.response;

import java.util.Map;

public class ErrorResponse {
    private String error;
    private String message;
    private Map<String, String> details; // opcional

    public ErrorResponse() {}

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(String error, String message, Map<String, String> details) {
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public String getError() { return error; }
    public String getMessage() { return message; }
    public Map<String, String> getDetails() { return details; }

    public void setError(String error) { this.error = error; }
    public void setMessage(String message) { this.message = message; }
    public void setDetails(Map<String, String> details) { this.details = details; }
}
