package com.espol.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.espol.entity.RegistroArchivo;

public interface ArchivoRepositorio extends JpaRepository<RegistroArchivo, Long> {

}
