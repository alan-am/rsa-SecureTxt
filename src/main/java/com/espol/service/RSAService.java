package com.espol.service;

import java.io.File;

import org.springframework.stereotype.Service;

import com.espol.service.estrategia.EstrategiaEncriptacion;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RSAService {
    private EstrategiaEncriptacion estrategia;

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
    
}
