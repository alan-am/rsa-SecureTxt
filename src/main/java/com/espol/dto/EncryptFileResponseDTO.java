package com.espol.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptFileResponseDTO {
    private String nombreArchivo;
    private String contenidoCifrado;
    private Long emisorId;
    private Long receptorId;
    private String mensaje;
}