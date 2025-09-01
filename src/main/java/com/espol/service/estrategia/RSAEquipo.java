package com.espol.service.estrategia;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
public static void encriptarArchivo(String rutaEntrada, String rutaSalida, int e, int n) throws Exception {
    if (n <= 255) {  // ESTO DEBEMOS CHEQUEARLO XQ SINO CAE LA WEB !!
        throw new IllegalArgumentException("n debe ser > 255 para poder cifrar cualquier byte.");
    }
    try (InputStream in = new FileInputStream(rutaEntrada);
         OutputStream out = new FileOutputStream(rutaSalida)) {

        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = in.read()) != -1) {
            int m = b & 0xFF; // 0..255
            int c = FuncionesAuxiliaresRSA.modPow(m, e, n);
            sb.append(c).append(' ');
        }
        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}

// Metodo para desencriptar un archivo .txt

public static void desencriptarArchivo(String rutaEntrada, String rutaSalida, int d, int n) throws Exception {
    String contenido;
    try (InputStream in = new FileInputStream(rutaEntrada)) {
        contenido = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }

    try (OutputStream out = new FileOutputStream(rutaSalida)) {
        String[] tokens = contenido.trim().split("\\s+"); // espacios, tabs o saltos de línea
        for (String t : tokens) {
            if (!t.isEmpty()) {
                int c = Integer.parseInt(t);
                int m = FuncionesAuxiliaresRSA.modPow(c, d, n);
                out.write((byte) (m & 0xFF)); // escribir el byte original
            }
        }
    }
}

}