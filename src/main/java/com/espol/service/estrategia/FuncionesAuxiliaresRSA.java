package com.espol.service.estrategia;

public class FuncionesAuxiliaresRSA {

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
}

