package com.espol.exception;

public class ArchivoException extends RuntimeException {
    
    public ArchivoException(String message) {
        super(message);
    }
    
    public ArchivoException(String message, Throwable cause) {
        super(message, cause);
    }
}