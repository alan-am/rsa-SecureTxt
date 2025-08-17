package com.espol.exception;

public class RSAException extends RuntimeException {
    
    public RSAException(String message) {
        super(message);
    }
    
    public RSAException(String message, Throwable cause) {
        super(message, cause);
    }
}