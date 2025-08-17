package com.espol.service.estrategia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAChatGPT implements EstrategiaEncriptacion {
    
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    
    private KeyPair keyPair;
    private String publicKeyString;
    private String privateKeyString;

    @Override
    public void generarClavePublica() {
        if (keyPair == null) {
            generarClaves();
        }
        PublicKey publicKey = keyPair.getPublic();
        publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("Clave pública RSA generada: " + publicKeyString);
    }

    @Override
    public void generarClavePrivada() {
        if (keyPair == null) {
            generarClaves();
        }
        PrivateKey privateKey = keyPair.getPrivate();
        privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        System.out.println("Clave privada RSA generada: " + privateKeyString);
    }

    @Override
    public void generarClaves() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            keyPair = keyPairGenerator.generateKeyPair();
            
            generarClavePublica();
            generarClavePrivada();
            
            System.out.println("Par de claves RSA generado exitosamente");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al generar claves RSA: " + e.getMessage());
            throw new RuntimeException("Error al generar claves RSA", e);
        }
    }

    @Override
    public void encriptarArchivo(File inputFile, String publicKey) {
        try {
            // Leer el contenido del archivo
            String contenido = leerArchivo(inputFile);
            
            // Encriptar el contenido
            String contenidoEncriptado = encriptarTexto(contenido, publicKey);
            
            // Crear archivo encriptado
            File archivoEncriptado = new File(inputFile.getParent(), 
                inputFile.getName().replaceFirst("[.][^.]+$", "") + "_encriptado.txt");
            
            escribirArchivo(archivoEncriptado, contenidoEncriptado);
            
            System.out.println("Archivo encriptado guardado como: " + archivoEncriptado.getName());
        } catch (Exception e) {
            System.err.println("Error al encriptar archivo: " + e.getMessage());
            throw new RuntimeException("Error al encriptar archivo", e);
        }
    }

    @Override
    public void desencriptarArchivo(File encryptedFile, String privateKey) {
        try {
            // Leer el contenido encriptado
            String contenidoEncriptado = leerArchivo(encryptedFile);
            
            // Desencriptar el contenido
            String contenidoDesencriptado = desencriptarTexto(contenidoEncriptado, privateKey);
            
            // Crear archivo desencriptado
            File archivoDesencriptado = new File(encryptedFile.getParent(), 
                encryptedFile.getName().replaceFirst("[.][^.]+$", "") + "_desencriptado.txt");
            
            escribirArchivo(archivoDesencriptado, contenidoDesencriptado);
            
            System.out.println("Archivo desencriptado guardado como: " + archivoDesencriptado.getName());
        } catch (Exception e) {
            System.err.println("Error al desencriptar archivo: " + e.getMessage());
            throw new RuntimeException("Error al desencriptar archivo", e);
        }
    }
    
    // Métodos auxiliares
    public String encriptarTexto(String texto, String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        byte[] textoBytes = texto.getBytes(StandardCharsets.UTF_8);
        byte[] textoEncriptado = cipher.doFinal(textoBytes);
        
        return Base64.getEncoder().encodeToString(textoEncriptado);
    }
    
    public String desencriptarTexto(String textoEncriptado, String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] textoEncriptadoBytes = Base64.getDecoder().decode(textoEncriptado);
        byte[] textoDesencriptado = cipher.doFinal(textoEncriptadoBytes);
        
        return new String(textoDesencriptado, StandardCharsets.UTF_8);
    }
    
    private String leerArchivo(File archivo) throws IOException {
        try (FileInputStream fis = new FileInputStream(archivo)) {
            byte[] contenido = fis.readAllBytes();
            return new String(contenido, StandardCharsets.UTF_8);
        }
    }
    
    private void escribirArchivo(File archivo, String contenido) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            fos.write(contenido.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    // Getters para obtener las claves generadas
    public String getPublicKeyString() {
        return publicKeyString;
    }
    
    public String getPrivateKeyString() {
        return privateKeyString;
    }
    
    public KeyPair getKeyPair() {
        return keyPair;
    }
}
