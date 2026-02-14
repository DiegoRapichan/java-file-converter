package com.fileconverter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de conversão de arquivo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionResponse {
    
    private boolean success;
    private String message;
    private String originalFileName;
    private String convertedFileName;
    private ConversionType conversionType;
    private long fileSizeBytes;
    private String downloadUrl;
    private String errorDetails;
}
