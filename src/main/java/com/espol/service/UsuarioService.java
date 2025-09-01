package com.espol.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.espol.dto.UsuarioRequestDTO;
import com.espol.dto.UsuarioResponseDTO;
import com.espol.entity.Usuario;
import com.espol.exception.UserNotFoundException;
import com.espol.repository.UsuarioRepositorio;
import com.espol.service.estrategia.FuncionesAuxiliaresRSA;
import com.espol.service.estrategia.RSAChatGPT;
import com.espol.service.estrategia.RSAEquipo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepositorio usuarioRepositorio;
    private final RSAChatGPT rsaChatGPT;
    private final Logger logger = Logger.getLogger(UsuarioService.class.getName());


    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        logger.info("Buscando usuario con ID...: " + id);
        Usuario usuario = usuarioRepositorio.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getE() + "," + usuario.getN()

        );
    }

    public List<UsuarioResponseDTO> listarTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioRepositorio.findAll();
        
        return usuarios.stream()
            .map(usuario -> new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getE() + "," + usuario.getN()
            ))
            .toList();
    }

    public Usuario obtenerUsuarioCompleto(Long id) {
        return usuarioRepositorio.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO requestDTO) {
        // Validar que el nombre no esté vacío
        if (requestDTO.getNombre() == null || requestDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario es requerido");
        }
        logger.info("Registrando usuario: " + requestDTO.getNombre());

        // Generar primos p y q
        int[] primos = FuncionesAuxiliaresRSA.obtener2PrimosRandom();
        int p = primos[0];
        int q = primos[1];
        logger.info("Primos generados: p=" + p + ", q=" + q);

        // Generar claves pública y privada para el user
        int[] llavePublica = RSAEquipo.generarLlavePublica(p, q);
        int e = llavePublica[0];
        int n = llavePublica[1];
        logger.info("Llave pública generada: e=" + e + ", n=" + n);

        int[] llavePrivada = RSAEquipo.generarLlavePrivada(e, p, q);
        int d = llavePrivada[0];
        logger.info("Llave privada generada: d=" + d);

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(requestDTO.getNombre().trim());
        usuario.setP(p);
        usuario.setQ(q);
        usuario.setN(n);
        usuario.setPhi((p - 1) * (q - 1));
        usuario.setE(e);
        usuario.setD(d);

        // Guardar usuario en db
        Usuario usuarioGuardado = usuarioRepositorio.save(usuario);
        logger.info("Usuario "+ usuarioGuardado.getNombre()+  " guardado con ID: " + usuarioGuardado.getId());

        // Convertir a DTO de respuesta (sin incluir clave privada)
        return new UsuarioResponseDTO(
            usuarioGuardado.getId(),
            usuarioGuardado.getNombre(),
            usuarioGuardado.getE() + "," + usuarioGuardado.getN() // clave publica (e,n)
        );
    }
}