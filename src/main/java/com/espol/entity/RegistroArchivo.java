package com.espol.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "archivo")
public class RegistroArchivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;       // nombre original del archivo
    @Column(name = "nombre_cifrado", nullable = false)
    private String nombreCifrado; // nombre del archivo cifrado
    @Column(name = "usuario_emisor", nullable = false)
    private Long EmisorId;         // FK a Usuario
    @Column(name = "usuario_receptor", nullable = false)
    private Long ReceptorId;       // FK a Usuario
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime createdAt;
}
