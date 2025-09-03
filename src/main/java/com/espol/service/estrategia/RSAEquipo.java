package com.espol.service.estrategia;
import java.nio.charset.StandardCharsets;

public class RSAEquipo{

// Metodo para generar llave publica 
public static int[] generarLlavePublica(int p, int q) {
    int n = p * q;
    int phi = (p - 1) * (q - 1);
    int e = 3;

    // buscar un e que sea coprimo con phi
    while (FuncionesAuxiliaresRSA.gcd(e, phi) != 1 && e < phi) {
        e += 2;
    }

    return new int[]{e, n}; // (e, n)
}


// Metodo para generar llave privada
public static int[] generarLlavePrivada(int e, int p, int q) {
    int phi = (p - 1) * (q - 1);
    int d = FuncionesAuxiliaresRSA.modInverse(e, phi);
    int n = p * q;
    return new int[]{d, n}; // (d, n)
}

// Metodo para encriptar un archivo .txt
public static String encriptarContenido(String contenido, int e, int n) throws Exception {
    if (n <= 255) {  
        throw new IllegalArgumentException("n debe ser > 255 para poder cifrar cualquier byte.");
    }

    StringBuilder sb = new StringBuilder();
    byte[] bytes = contenido.getBytes(StandardCharsets.UTF_8);

    for (byte b : bytes) {
        int m = b & 0xFF; // Asegurar rango 0-255
        int c = FuncionesAuxiliaresRSA.modPow(m, e, n); // Encriptamos
        sb.append(c).append(' '); // Guardamos con espacio cada caracter encriptado
    }

    return sb.toString().trim(); 
}

// Metodo para desencriptar un archivo .txt
public static String desencriptarContenido(String contenidoCifrado, int d, int n){
    StringBuilder resultado = new StringBuilder();

    String[] tokens = contenidoCifrado.trim().split("\\s+"); // espacios, tabs o saltos de línea
    for (String t : tokens) {
        if (!t.isEmpty()) {
            int c = Integer.parseInt(t);
            int m = FuncionesAuxiliaresRSA.modPow(c, d, n); //ojo agregar excepcion a modPow para evitar errore
            resultado.append((char) (m & 0xFF)); // reconstruir el carácter original
        }
    }

    return resultado.toString();
}

}