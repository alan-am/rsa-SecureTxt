package com.espol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoEncriptadoDTO {
    private String contenidoEncriptado;
    private String nombreArchivo;
    private Long destinatarioId;
    private String mensaje;
}