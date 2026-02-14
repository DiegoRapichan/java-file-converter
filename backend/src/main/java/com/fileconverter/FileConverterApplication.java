package com.fileconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * File Converter API - Spring Boot Application
 * 
 * API REST para conversão de arquivos entre diferentes formatos:
 * - CSV ↔ JSON
 * - JSON ↔ XML
 * - CSV → Excel
 * - Texto → PDF
 * - JSON → PDF
 * 
 * @author Diego Rapichan
 * @version 1.0.0
 */
@SpringBootApplication
public class FileConverterApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileConverterApplication.class, args);
    }
}
