package com.espol.service.estrategia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.KeyFactory;

import javax.crypto.Cipher;

import com.espol.exception.RSAException;
import com.espol.exception.FileProcessingException;

public class RSAChatGPT implements EstrategiaEncriptacion {
    
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private static final String ALGORITHM = "RSA";
    private static final int KEY_LENGTH = 2048;

    @Override
    public void generarClavePublica() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_LENGTH);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new RSAException("Error al generar clave pública: " + e.getMessage(), e);
        }
    }

    @Override
    public void generarClavePrivada() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_LENGTH);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new RSAException("Error al generar clave privada: " + e.getMessage(), e);
        }
    }

    @Override
    public void generarClaves() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_LENGTH);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new RSAException("Error al generar claves RSA: " + e.getMessage(), e);
        }
    }

    public KeyPair generarParClaves() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_LENGTH);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RSAException("Error al generar par de claves RSA: " + e.getMessage(), e);
        }
    }

    @Override
    public void encriptarArchivo(File inputFile, String publicKeyString) {
        try {
            PublicKey publicKey = getPublicKeyFromString(publicKeyString);
            
            // Leer el contenido del archivo
            byte[] fileContent = readFileContent(inputFile);
            
            // Encriptar el contenido
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            // RSA puede encriptar solo bloques pequeños, así que dividimos el contenido
            byte[] encryptedContent = encryptInBlocks(cipher, fileContent);
            
            // Crear archivo cifrado
            String encryptedFileName = inputFile.getParent() + "/encrypted_" + inputFile.getName();
            writeFileContent(new File(encryptedFileName), encryptedContent);
            
        } catch (Exception e) {
            throw new FileProcessingException("Error al encriptar archivo: " + e.getMessage(), e);
        }
    }

    @Override
    public void desencriptarArchivo(File encryptedFile, String privateKeyString) {
        try {
            PrivateKey privateKey = getPrivateKeyFromString(privateKeyString);
            
            // Leer el contenido cifrado
            byte[] encryptedContent = readFileContent(encryptedFile);
            
            // Desencriptar el contenido
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            // Desencriptar en bloques
            byte[] decryptedContent = decryptInBlocks(cipher, encryptedContent);
            
            // Crear archivo desencriptado
            String decryptedFileName = encryptedFile.getParent() + "/decrypted_" + 
                encryptedFile.getName().replace("encrypted_", "");
            writeFileContent(new File(decryptedFileName), decryptedContent);
            
        } catch (Exception e) {
            throw new FileProcessingException("Error al desencriptar archivo: " + e.getMessage(), e);
        }
    }

    public String encriptarContenido(String contenido, String publicKeyString) {
        try {
            PublicKey publicKey = getPublicKeyFromString(publicKeyString);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            byte[] contentBytes = contenido.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = encryptInBlocks(cipher, contentBytes);
            
            return Base64.getEncoder().encodeToString(encryptedBytes);
            
        } catch (Exception e) {
            throw new RSAException("Error al encriptar contenido: " + e.getMessage(), e);
        }
    }

    public String desencriptarContenido(String contenidoCifrado, String privateKeyString) {
        try {
            PrivateKey privateKey = getPrivateKeyFromString(privateKeyString);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            byte[] encryptedBytes = Base64.getDecoder().decode(contenidoCifrado);
            byte[] decryptedBytes = decryptInBlocks(cipher, encryptedBytes);
            
            return new String(decryptedBytes, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            throw new RSAException("Error al desencriptar contenido: " + e.getMessage(), e);
        }
    }

    // Métodos auxiliares
    private PublicKey getPublicKeyFromString(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    private PrivateKey getPrivateKeyFromString(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    public String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    private byte[] readFileContent(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        }
    }

    private void writeFileContent(File file, byte[] content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        }
    }

    private byte[] encryptInBlocks(Cipher cipher, byte[] data) throws Exception {
        int blockSize = 245; // Para claves de 2048 bits, el tamaño máximo es 245 bytes
        int dataLength = data.length;
        byte[] result = new byte[0];
        
        for (int i = 0; i < dataLength; i += blockSize) {
            int endIndex = Math.min(i + blockSize, dataLength);
            byte[] block = new byte[endIndex - i];
            System.arraycopy(data, i, block, 0, endIndex - i);
            
            byte[] encryptedBlock = cipher.doFinal(block);
            byte[] newResult = new byte[result.length + encryptedBlock.length];
            System.arraycopy(result, 0, newResult, 0, result.length);
            System.arraycopy(encryptedBlock, 0, newResult, result.length, encryptedBlock.length);
            result = newResult;
        }
        
        return result;
    }

    private byte[] decryptInBlocks(Cipher cipher, byte[] data) throws Exception {
        int blockSize = 256; // Para claves de 2048 bits, el bloque cifrado es de 256 bytes
        int dataLength = data.length;
        byte[] result = new byte[0];
        
        for (int i = 0; i < dataLength; i += blockSize) {
            byte[] block = new byte[blockSize];
            System.arraycopy(data, i, block, 0, blockSize);
            
            byte[] decryptedBlock = cipher.doFinal(block);
            byte[] newResult = new byte[result.length + decryptedBlock.length];
            System.arraycopy(result, 0, newResult, 0, result.length);
            System.arraycopy(decryptedBlock, 0, newResult, result.length, decryptedBlock.length);
            result = newResult;
        }
        
        return result;
    }
}
