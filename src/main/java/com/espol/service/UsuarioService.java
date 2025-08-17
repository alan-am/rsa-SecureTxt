package com.espol.service;

import java.security.KeyPair;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.espol.dto.UsuarioRequestDTO;
import com.espol.dto.UsuarioResponseDTO;
import com.espol.entity.Usuario;
import com.espol.exception.UserNotFoundException;
import com.espol.repository.UsuarioRepositorio;
import com.espol.service.estrategia.RSAChatGPT;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepositorio usuarioRepositorio;
    private final RSAChatGPT rsaChatGPT;

    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO requestDTO) {
        // Validar que el nombre no esté vacío
        if (requestDTO.getNombre() == null || requestDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario es requerido");
        }

        // Generar par de claves RSA
        KeyPair keyPair = rsaChatGPT.generarParClaves();
        String clavePublica = rsaChatGPT.publicKeyToString(keyPair.getPublic());
        String clavePrivada = rsaChatGPT.privateKeyToString(keyPair.getPrivate());

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(requestDTO.getNombre().trim());
        usuario.setClavePublica(clavePublica);
        usuario.setClavePrivada(clavePrivada);

        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepositorio.save(usuario);

        // Convertir a DTO de respuesta (sin incluir clave privada)
        return new UsuarioResponseDTO(
            usuarioGuardado.getId(),
            usuarioGuardado.getNombre(),
            usuarioGuardado.getClavePublica()
        );
    }

    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepositorio.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getClavePublica()
        );
    }

    public List<UsuarioResponseDTO> listarTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioRepositorio.findAll();
        
        return usuarios.stream()
            .map(usuario -> new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getClavePublica()
            ))
            .toList();
    }

    public Usuario obtenerUsuarioCompleto(Long id) {
        return usuarioRepositorio.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    public String obtenerClavePublica(Long userId) {
        Usuario usuario = obtenerUsuarioCompleto(userId);
        return usuario.getClavePublica();
    }

    public String obtenerClavePrivada(Long userId) {
        Usuario usuario = obtenerUsuarioCompleto(userId);
        return usuario.getClavePrivada();
    }
}