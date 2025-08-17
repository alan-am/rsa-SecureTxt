package com.espol.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioNotFoundException(UsuarioNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Usuario no encontrado");
        error.put("message", ex.getMessage());
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ArchivoException.class)
    public ResponseEntity<Map<String, String>> handleArchivoException(ArchivoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error de archivo");
        error.put("message", ex.getMessage());
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error de ejecución");
        error.put("message", ex.getMessage());
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Argumento inválido");
        error.put("message", ex.getMessage());
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Archivo demasiado grande");
        error.put("message", "El tamaño del archivo excede el límite permitido");
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        error.put("message", "Ha ocurrido un error inesperado");
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}