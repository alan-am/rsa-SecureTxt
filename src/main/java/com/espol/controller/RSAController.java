package com.espol.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.espol.dto.DecryptFileResponseDTO;
import com.espol.dto.EncryptFileResponseDTO;
import com.espol.service.RSAService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rsa")
@RequiredArgsConstructor
public class RSAController {

    private final RSAService rsaService;

    @PostMapping("/encriptar")
    public ResponseEntity<EncryptFileResponseDTO> encriptarArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("receptorId") Long receptorId,
            @RequestParam("emisorId") Long emisorId) {
        
        EncryptFileResponseDTO response = rsaService.encriptarArchivo(archivo, receptorId, emisorId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/desencriptar")
    public ResponseEntity<DecryptFileResponseDTO> desencriptarArchivo(
            @RequestParam("contenidoCifrado") String contenidoCifrado,
            @RequestParam("nombreArchivo") String nombreArchivo,
            @RequestParam("usuarioId") Long usuarioId) {
        
        DecryptFileResponseDTO response = rsaService.desencriptarArchivo(contenidoCifrado, nombreArchivo, usuarioId);
        return ResponseEntity.ok(response);
    }
}