package com.espol.service.estrategia;

import java.io.File;

public interface EstrategiaEncriptacion {
    void generarClavePublica();
    void generarClavePrivada();
    void generarClaves();
    void encriptarArchivo(File inputFile, String publicKey);
    void desencriptarArchivo(File encryptedFile, String privateKey);
}
