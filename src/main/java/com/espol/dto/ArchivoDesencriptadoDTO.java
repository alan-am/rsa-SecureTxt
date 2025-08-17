package com.espol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoDesencriptadoDTO {
    private String contenidoDesencriptado;
    private String nombreArchivo;
    private Long usuarioId;
    private String mensaje;
}