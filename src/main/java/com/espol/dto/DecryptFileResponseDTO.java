package com.espol.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DecryptFileResponseDTO {
    private String nombreArchivo;
    private String contenidoDescifrado;
    private Long usuarioId;
    private String mensaje;
}