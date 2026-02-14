package com.fileconverter.controller;

import com.fileconverter.model.ConversionResponse;
import com.fileconverter.model.ConversionType;
import com.fileconverter.service.FileConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller para conversão de arquivos
 */
@Slf4j
@RestController
@RequestMapping("/api/convert")
@CrossOrigin(origins = "*")
@Tag(name = "File Converter", description = "Endpoints para conversão de arquivos entre diferentes formatos")
public class FileConverterController {

    private final FileConversionService fileConversionService;

    public FileConverterController(FileConversionService fileConversionService) {
        this.fileConversionService = fileConversionService;
    }

    @GetMapping("/types")
    @Operation(summary = "Listar tipos de conversão", description = "Retorna todos os tipos de conversão suportados")
    public ResponseEntity<List<ConversionTypeDTO>> getSupportedConversions() {
        List<ConversionTypeDTO> types = Arrays.stream(ConversionType.values())
                .map(type -> new ConversionTypeDTO(type.name(), type.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Converter arquivo", description = "Faz upload e converte um arquivo para o formato especificado")
    public ResponseEntity<ConversionResponse> convertFile(
            @Parameter(description = "Arquivo para conversão", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Tipo de conversão", required = true, example = "CSV_TO_JSON")
            @RequestParam("conversionType") ConversionType conversionType
    ) {
        log.info("Received conversion request: {} to {}", file.getOriginalFilename(), conversionType);
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ConversionResponse.builder()
                            .success(false)
                            .message("File is empty")
                            .build());
        }
        
        ConversionResponse response = fileConversionService.convertFile(file, conversionType);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/download/{fileName}")
    @Operation(summary = "Baixar arquivo convertido", description = "Faz download do arquivo convertido")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Nome do arquivo", required = true)
            @PathVariable String fileName
    ) throws IOException {
        log.info("Download request for file: {}", fileName);
        
        File file = fileConversionService.getConvertedFile(fileName);
        Resource resource = new FileSystemResource(file);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica se a API está funcionando")
    public ResponseEntity<HealthResponse> healthCheck() {
        return ResponseEntity.ok(new HealthResponse("File Converter API is running", "1.0.0"));
    }

    // DTOs internos
    public record ConversionTypeDTO(String type, String description) {}
    public record HealthResponse(String status, String version) {}
}
