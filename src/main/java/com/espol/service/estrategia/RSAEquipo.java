package com.espol.service.estrategia;

import java.io.File;

public class RSAEquipo implements EstrategiaEncriptacion{
    @Override
    public void generarClavePublica() {
        // Implementación para generar clave pública
    }

    @Override
    public void generarClavePrivada() {
        // Implementación para generar clave privada
    }

    @Override
    public void generarClaves() {
        // Implementación para generar ambas claves
    }

    @Override
    public void encriptarArchivo(File inputFile, String publicKey) {
        // Implementación para encriptar archivo usando la clave pública
    }

    @Override
    public void desencriptarArchivo(File encryptedFile, String privateKey) {
        // Implementación para desencriptar archivo usando la clave privada
    }

}
