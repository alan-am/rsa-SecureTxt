package com.espol.service;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.espol.dto.DecryptFileResponseDTO;
import com.espol.dto.EncryptFileResponseDTO;
import com.espol.entity.RegistroArchivo;
import com.espol.entity.Usuario;
import com.espol.exception.FileProcessingException;
import com.espol.exception.RSAException;
import com.espol.repository.ArchivoRepositorio;
import com.espol.repository.UsuarioRepositorio;
import com.espol.service.estrategia.FuncionesAuxiliaresRSA;
import com.espol.service.estrategia.RSAEquipo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RSAService {
    
    private final UsuarioService usuarioService;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ArchivoRepositorio archivoRepositorio;
    private final Logger logger = Logger.getLogger(RSAService.class.getName());


    public EncryptFileResponseDTO encriptarArchivo(MultipartFile archivo, Long receptorId, Long emisorId) {
        try {
            // Validar que el archivo sea .txt
            if (!archivo.getOriginalFilename().endsWith(".txt")) {
                throw new FileProcessingException("Solo se permiten archivos .txt");
            }

            //buscando usuario en la db
            Usuario receptor = usuarioRepositorio.findById(receptorId)
                .orElseThrow(() -> new IllegalArgumentException("Receptor no encontrado con ID: " + receptorId));
            logger.info("Receptor encontrado: " + receptor.getNombre());

            // Cifrar archivo usando e y n del receptor
            String contenidoArchivo = FuncionesAuxiliaresRSA.devolverContenido(archivo);
            String contenidoCifrado = RSAEquipo.encriptarContenido(contenidoArchivo, receptor.getE(), receptor.getN());
            logger.info("contenido del archivo encriptado completo.");

            // Registrar archivo en db
            RegistroArchivo registro = new RegistroArchivo();
            registro.setNombre(archivo.getOriginalFilename());
            registro.setNombreCifrado("encrypted_" + archivo.getOriginalFilename());
            registro.setEmisorId(emisorId);
            registro.setReceptorId(receptorId);
            registro.setCreatedAt(LocalDateTime.now());
            archivoRepositorio.save(registro);
            logger.info("Registro de archivo guardado en la base de datos.");

            //devolviendo respuesta a front
            return new EncryptFileResponseDTO(
                archivo.getOriginalFilename(),
                contenidoCifrado,
                emisorId,
                receptorId,
                "Archivo encriptado exitosamente"
            );
        }catch (Exception e) {
            throw new RSAException("Error al encriptar el archivo: " + e.getMessage(), e);
        }
    }

    public DecryptFileResponseDTO desencriptarArchivo(String contenidoCifrado, String nombreArchivo, Long usuarioId) {

        //buscando usuario en la db
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));
        logger.info("Usuario encontrado: " + usuario.getNombre());

        //obteniendo d y n del usuario
        int d = usuario.getD();
        int n = usuario.getN();
        logger.info("Obteniendo d y n del usuario: d=" + d + ", n=" + n);
        
        String contenidoDescifrado = RSAEquipo.desencriptarContenido(contenidoCifrado, d, n);
        logger.info("Contenido del archivo desencriptado.");

        return new DecryptFileResponseDTO(
            nombreArchivo,
            contenidoDescifrado,
            usuarioId,
            "Archivo desencriptado exitosamente"
        );
    }
}

