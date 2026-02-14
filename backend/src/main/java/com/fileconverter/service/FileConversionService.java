package com.fileconverter.service;

import com.fileconverter.converter.FileConverter;
import com.fileconverter.factory.ConverterFactory;
import com.fileconverter.model.ConversionResponse;
import com.fileconverter.model.ConversionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service responsável pela lógica de conversão de arquivos
 */
@Slf4j
@Service
public class FileConversionService {

    private final ConverterFactory converterFactory;
    private final String OUTPUT_DIR = "output/";

    public FileConversionService(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
        createOutputDirectory();
    }

    /**
     * Converte um arquivo para o formato especificado
     * 
     * @param file Arquivo de entrada
     * @param conversionType Tipo de conversão
     * @return ConversionResponse com detalhes da conversão
     */
    public ConversionResponse convertFile(MultipartFile file, ConversionType conversionType) {
        try {
            log.info("Starting conversion: {} -> {}", file.getOriginalFilename(), conversionType);
            
            // Obtém o conversor apropriado
            FileConverter converter = converterFactory.getConverter(conversionType);
            
            // Gera nome único para arquivo de saída
            String outputFileName = generateOutputFileName(file.getOriginalFilename(), conversionType);
            Path outputPath = Paths.get(OUTPUT_DIR + outputFileName);
            
            // Realiza a conversão
            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
                
                converter.convert(inputStream, outputStream);
            }
            
            // Calcula tamanho do arquivo
            long fileSize = Files.size(outputPath);
            
            log.info("Conversion successful: {}", outputFileName);
            
            return ConversionResponse.builder()
                    .success(true)
                    .message("File converted successfully")
                    .originalFileName(file.getOriginalFilename())
                    .convertedFileName(outputFileName)
                    .conversionType(conversionType)
                    .fileSizeBytes(fileSize)
                    .downloadUrl("/api/convert/download/" + outputFileName)
                    .build();
                    
        } catch (Exception e) {
            log.error("Conversion failed", e);
            return ConversionResponse.builder()
                    .success(false)
                    .message("Conversion failed")
                    .originalFileName(file.getOriginalFilename())
                    .conversionType(conversionType)
                    .errorDetails(e.getMessage())
                    .build();
        }
    }

    /**
     * Retorna um arquivo convertido para download
     * 
     * @param fileName Nome do arquivo
     * @return File
     */
    public File getConvertedFile(String fileName) throws IOException {
        Path filePath = Paths.get(OUTPUT_DIR + fileName);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + fileName);
        }
        return filePath.toFile();
    }

    /**
     * Gera nome único para arquivo de saída
     */
    private String generateOutputFileName(String originalFileName, ConversionType conversionType) {
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String extension = getOutputExtension(conversionType);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return baseName + "_" + uniqueId + "." + extension;
    }

    /**
     * Retorna a extensão do arquivo de saída baseado no tipo de conversão
     */
    private String getOutputExtension(ConversionType conversionType) {
        return switch (conversionType) {
            case CSV_TO_JSON, XML_TO_JSON -> "json";
            case JSON_TO_CSV -> "csv";
            case JSON_TO_XML -> "xml";
            case CSV_TO_EXCEL -> "xlsx";
            case TEXT_TO_PDF, JSON_TO_PDF -> "pdf";
        };
    }

    /**
     * Cria o diretório de saída se não existir
     */
    private void createOutputDirectory() {
        try {
            Path outputPath = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
                log.info("Output directory created: {}", OUTPUT_DIR);
            }
        } catch (IOException e) {
            log.error("Failed to create output directory", e);
        }
    }
}
