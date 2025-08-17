package com.espol.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.espol.dto.DecryptFileResponseDTO;
import com.espol.dto.EncryptFileResponseDTO;
import com.espol.entity.RegistroArchivo;
import com.espol.exception.FileProcessingException;
import com.espol.exception.RSAException;
import com.espol.repository.ArchivoRepositorio;
import com.espol.service.estrategia.RSAChatGPT;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RSAService {
    
    private final RSAChatGPT rsaChatGPT;
    private final UsuarioService usuarioService;
    private final ArchivoRepositorio archivoRepositorio;

    public EncryptFileResponseDTO encriptarArchivo(MultipartFile archivo, Long receptorId, Long emisorId) {
        try {
            // Validar que el archivo sea .txt
            if (!archivo.getOriginalFilename().endsWith(".txt")) {
                throw new FileProcessingException("Solo se permiten archivos .txt");
            }

            // Obtener clave pública del receptor
            String clavePublica = usuarioService.obtenerClavePublica(receptorId);
            
            // Leer contenido del archivo
            String contenido = new String(archivo.getBytes());
            
            // Encriptar contenido
            String contenidoCifrado = rsaChatGPT.encriptarContenido(contenido, clavePublica);
            
            // Registrar archivo en base de datos
            RegistroArchivo registro = new RegistroArchivo();
            registro.setNombre(archivo.getOriginalFilename());
            registro.setNombreCifrado("encrypted_" + archivo.getOriginalFilename());
            registro.setEmisorId(emisorId);
            registro.setReceptorId(receptorId);
            registro.setCreatedAt(LocalDateTime.now());
            
            archivoRepositorio.save(registro);
            
            return new EncryptFileResponseDTO(
                archivo.getOriginalFilename(),
                contenidoCifrado,
                emisorId,
                receptorId,
                "Archivo encriptado exitosamente"
            );
            
        } catch (IOException e) {
            throw new FileProcessingException("Error al procesar el archivo: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RSAException("Error al encriptar el archivo: " + e.getMessage(), e);
        }
    }

    public DecryptFileResponseDTO desencriptarArchivo(String contenidoCifrado, String nombreArchivo, Long usuarioId) {
        try {
            // Obtener clave privada del usuario
            String clavePrivada = usuarioService.obtenerClavePrivada(usuarioId);
            
            // Desencriptar contenido
            String contenidoDescifrado = rsaChatGPT.desencriptarContenido(contenidoCifrado, clavePrivada);
            
            return new DecryptFileResponseDTO(
                nombreArchivo,
                contenidoDescifrado,
                usuarioId,
                "Archivo desencriptado exitosamente"
            );
            
        } catch (Exception e) {
            throw new RSAException("Error al desencriptar el archivo: " + e.getMessage(), e);
        }
    }

    // Métodos para compatibilidad con la interfaz anterior
    public void generarClavePublica() {
        rsaChatGPT.generarClavePublica();
    }

    public void generarClavePrivada() {
        rsaChatGPT.generarClavePrivada();
    }
    
    public void generarClaves() {
        rsaChatGPT.generarClaves();
    }

    public void encriptarArchivo(File inputFile, String publicKey) {
        rsaChatGPT.encriptarArchivo(inputFile, publicKey);
    }

    public void desencriptarArchivo(File encryptedFile, String privateKey) {
        rsaChatGPT.desencriptarArchivo(encryptedFile, privateKey);
    }
}
