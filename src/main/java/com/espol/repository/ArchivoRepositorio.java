package com.espol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.espol.entity.RegistroArchivo;

@Repository
public interface ArchivoRepositorio extends JpaRepository<RegistroArchivo, Long> {

}
