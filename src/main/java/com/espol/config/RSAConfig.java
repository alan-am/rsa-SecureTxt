package com.espol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.espol.service.estrategia.RSAChatGPT;

@Configuration
public class RSAConfig {

    @Bean
    public RSAChatGPT rsaChatGPT() {
        return new RSAChatGPT();
    }
}