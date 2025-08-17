package com.espol.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put("error", "Usuario no encontrado");
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RSAException.class)
    public ResponseEntity<Map<String, Object>> handleRSAException(
            RSAException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("error", "Error de encriptación RSA");
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleFileProcessingException(
            FileProcessingException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("error", "Error de procesamiento de archivo");
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("error", "Argumento inválido");
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("error", "Error interno del servidor");
        errorDetails.put("message", "Ha ocurrido un error inesperado");
        errorDetails.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}