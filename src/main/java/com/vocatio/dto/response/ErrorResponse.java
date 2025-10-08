package com.vocatio.dto.response;

public class ErrorResponse {
    private String error;
    private String message;

    public ErrorResponse() {}
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() { return error; }
    public String getMessage() { return message; }
    public void setError(String error) { this.error = error; }
    public void setMessage(String message) { this.message = message; }
}