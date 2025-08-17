package com.espol.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.espol.dto.ArchivoDesencriptadoDTO;
import com.espol.dto.ArchivoEncriptadoDTO;
import com.espol.dto.UsuarioDTO;
import com.espol.entity.Usuario;
import com.espol.exception.ArchivoException;
import com.espol.exception.UsuarioNotFoundException;
import com.espol.service.RSAService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/rsa")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class RSAController {

    @Autowired
    private RSAService rsaService;

    // Endpoint 1: Registrar un nuevo usuario y generar par de claves RSA
    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestParam String nombre) {
        try {
            Usuario usuario = rsaService.crearUsuarioConClaves(nombre);
            UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getClavePublica());
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear usuario: " + e.getMessage(), e);
        }
    }

    // Endpoint 2: Búsqueda por usuario por ID específico
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorId(@PathVariable Long id) {
        try {
            Usuario usuario = rsaService.buscarUsuarioPorId(id);
            UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getClavePublica());
            return ResponseEntity.ok(usuarioDTO);
        } catch (UsuarioNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuario: " + e.getMessage(), e);
        }
    }

    // Endpoint 3: Listar todos los usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarTodosLosUsuarios() {
        try {
            List<Usuario> usuarios = rsaService.listarTodosLosUsuarios();
            List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(u -> new UsuarioDTO(u.getId(), u.getNombre(), u.getClavePublica()))
                .collect(Collectors.toList());
            return ResponseEntity.ok(usuariosDTO);
        } catch (Exception e) {
            throw new RuntimeException("Error al listar usuarios: " + e.getMessage(), e);
        }
    }

    // Endpoint 4: Recibe un archivo .txt y el ID del destinatario. 
    // Devuelve archivo cifrado con RSA usando la clave pública del receptor.
    @PostMapping("/encriptar")
    public ResponseEntity<ArchivoEncriptadoDTO> encriptarArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("destinatarioId") Long destinatarioId) {
        
        try {
            // Validar que el archivo sea .txt
            if (!archivo.getOriginalFilename().toLowerCase().endsWith(".txt")) {
                throw new IllegalArgumentException("Solo se permiten archivos .txt");
            }

            // Crear archivo temporal
            File archivoTemp = File.createTempFile("temp_", ".txt");
            archivo.transferTo(archivoTemp);

            // Encriptar archivo
            String contenidoEncriptado = rsaService.encriptarArchivoParaUsuario(archivoTemp, destinatarioId);

            // Limpiar archivo temporal
            archivoTemp.delete();

            ArchivoEncriptadoDTO respuesta = new ArchivoEncriptadoDTO(
                contenidoEncriptado,
                archivo.getOriginalFilename(),
                destinatarioId,
                "Archivo encriptado exitosamente"
            );

            return ResponseEntity.ok(respuesta);
        } catch (IOException e) {
            throw new ArchivoException("Error al procesar el archivo: " + e.getMessage(), e);
        } catch (ArchivoException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar archivo: " + e.getMessage(), e);
        }
    }

    // Endpoint 5: Recibe un archivo cifrado .txt y el ID del usuario que hace la petición. 
    // Devuelve contenido descifrado usando la clave privada del usuario.
    @PostMapping("/desencriptar")
    public ResponseEntity<ArchivoDesencriptadoDTO> desencriptarArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("usuarioId") Long usuarioId) {
        
        try {
            // Validar que el archivo sea .txt
            if (!archivo.getOriginalFilename().toLowerCase().endsWith(".txt")) {
                throw new IllegalArgumentException("Solo se permiten archivos .txt");
            }

            // Leer contenido del archivo
            String contenidoEncriptado = new String(archivo.getBytes());

            // Desencriptar archivo
            String contenidoDesencriptado = rsaService.desencriptarArchivoParaUsuario(contenidoEncriptado, usuarioId);

            ArchivoDesencriptadoDTO respuesta = new ArchivoDesencriptadoDTO(
                contenidoDesencriptado,
                archivo.getOriginalFilename(),
                usuarioId,
                "Archivo desencriptado exitosamente"
            );

            return ResponseEntity.ok(respuesta);
        } catch (ArchivoException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al desencriptar archivo: " + e.getMessage(), e);
        }
    }

    // Endpoint adicional: Generar claves RSA (para pruebas)
    @PostMapping("/generar-claves")
    public ResponseEntity<String> generarClaves() {
        try {
            rsaService.generarClaves();
            return ResponseEntity.ok("Claves RSA generadas exitosamente");
        } catch (Exception e) {
            throw new RuntimeException("Error al generar claves: " + e.getMessage(), e);
        }
    }
}