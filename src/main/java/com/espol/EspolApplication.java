package com.espol;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EspolApplication {

    public static void main(String[] args) {

        // Verificacion archivo .env
        File envFile = new File(".env");
        if (envFile.exists()) {
            System.out.println("Archivo .env local encontrado. Cargando variables...");
            Dotenv dotenv = Dotenv.load();

            System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
            System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
            System.setProperty("DB_USER", dotenv.get("DB_USER"));
		    System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
            System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        }
        SpringApplication.run(EspolApplication.class, args);
    }

}