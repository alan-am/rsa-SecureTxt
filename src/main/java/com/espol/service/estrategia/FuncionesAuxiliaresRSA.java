package com.espol.service.estrategia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.Normalizer;

import org.springframework.web.multipart.MultipartFile;

public class FuncionesAuxiliaresRSA {
    private static final SecureRandom random = new SecureRandom();


    // Calcula el MCD usando Euclides
    public static int gcd(int a, int b) {
        while (b != 0) {
            int tmp = b;
            b = a % b;
            a = tmp;
        }
        return a;
    }

    // Calcula el inverso modular usando el algoritmo extendido de Euclides
    public static int modInverse(int e, int phi) {
        int t = 0, newT = 1;
        int r = phi, newR = e;

        while (newR != 0) {
            int quotient = r / newR;

            int tempT = newT;
            newT = t - quotient * newT;
            t = tempT;

            int tempR = newR;
            newR = r - quotient * newR;
            r = tempR;
        }

        if (r > 1) throw new ArithmeticException("No hay inverso modular");
        if (t < 0) t += phi;

        return t;
    }

    // Potenciación modular eficiente (a^b mod n)
    public static int modPow(int base, int exp, int mod) {
        int result = 1;
        base = base % mod;

        while (exp > 0) {
            if ((exp & 1) == 1)
                result = (result * base) % mod;
            exp >>= 1;
            base = (base * base) % mod;
        }

        return result;
    }


    //generacion de primos

    public static int[] obtener2PrimosRandom() {
        int p, q;

        do {
            p = primoRandom();
            q = primoRandom();
        } while (p == q); // asegurar que sean distintos

        return new int[]{p, q};
    }

    private static int primoRandom() {
        int prime;
        // generacion de primos de aproximadamente hasta 512
        prime = BigInteger.probablePrime(6, random).intValue();
        return prime;
    }

    // Método auxiliar para quitar tildes y reemplazar ñ por n
    private static String normalizarTexto(String texto) {
        if (texto == null) return null;

        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        // Eliminar acentos
        normalizado = normalizado.replaceAll("\\p{M}", "");
        // Reemplazar ñ por n
        normalizado = normalizado.replace("ñ", "n").replace("Ñ", "N");

        return normalizado;
    }

    // leer contenido archivos
    public static String devolverContenido(MultipartFile multipartFile) {
        StringBuilder contenido = new StringBuilder();
    
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8)
            )
        ) {
            String linea;
            while ((linea = reader.readLine()) != null) {

                String lineaNormalizada = normalizarTexto(linea);
                contenido.append(lineaNormalizada).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo", e);
        }

        return contenido.toString();
    }




}

