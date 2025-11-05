package com.vocatio.exception;

public class AccessNotAuthorizedException extends RuntimeException {
    public AccessNotAuthorizedException(String message) {
        super(message);
    }
}
