package com.espol.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.espol.entity.Usuario;
import com.espol.exception.ArchivoException;
import com.espol.exception.UsuarioNotFoundException;
import com.espol.repository.UsuarioRepositorio;
import com.espol.service.estrategia.EstrategiaEncriptacion;
import com.espol.service.estrategia.RSAChatGPT;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RSAService {
    private EstrategiaEncriptacion estrategia;
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public void setEstrategia(EstrategiaEncriptacion estrategia) {
        this.estrategia = estrategia;
    }

    public void generarClavePublica() {
        estrategia.generarClavePublica();
    }

    public void generarClavePrivada() {
        estrategia.generarClavePrivada();
    }
    
    public void generarClaves() {
        estrategia.generarClaves();
    }

    public void encriptarArchivo(File inputFile, String publicKey) {
        estrategia.encriptarArchivo(inputFile, publicKey);
    }

    public void desencriptarArchivo(File encryptedFile, String privateKey) {
        estrategia.desencriptarArchivo(encryptedFile, privateKey);
    }
    
    // Métodos para manejo de usuarios
    public Usuario crearUsuarioConClaves(String nombre) {
        // Crear nueva instancia de RSAChatGPT para generar claves únicas
        RSAChatGPT rsaChatGPT = new RSAChatGPT();
        rsaChatGPT.generarClaves();
        
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setClavePublica(rsaChatGPT.getPublicKeyString());
        usuario.setClavePrivada(rsaChatGPT.getPrivateKeyString());
        
        return usuarioRepositorio.save(usuario);
    }
    
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepositorio.findById(id)
            .orElseThrow(() -> new UsuarioNotFoundException(id));
    }
    
    public List<Usuario> listarTodosLosUsuarios() {
        return usuarioRepositorio.findAll();
    }
    
    public String encriptarArchivoParaUsuario(File archivo, Long usuarioDestinatarioId) {
        Usuario destinatario = buscarUsuarioPorId(usuarioDestinatarioId);
        
        try {
            RSAChatGPT rsaChatGPT = new RSAChatGPT();
            return rsaChatGPT.encriptarTexto(leerArchivo(archivo), destinatario.getClavePublica());
        } catch (Exception e) {
            throw new ArchivoException("Error al encriptar archivo: " + e.getMessage(), e);
        }
    }
    
    public String desencriptarArchivoParaUsuario(String contenidoEncriptado, Long usuarioId) {
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        
        try {
            RSAChatGPT rsaChatGPT = new RSAChatGPT();
            return rsaChatGPT.desencriptarTexto(contenidoEncriptado, usuario.getClavePrivada());
        } catch (Exception e) {
            throw new ArchivoException("Error al desencriptar archivo: " + e.getMessage(), e);
        }
    }
    
    private String leerArchivo(File archivo) throws Exception {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(archivo)) {
            byte[] contenido = fis.readAllBytes();
            return new String(contenido, java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}
