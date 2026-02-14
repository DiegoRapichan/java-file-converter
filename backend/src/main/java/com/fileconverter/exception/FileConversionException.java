package com.fileconverter.exception;

/**
 * Exception customizada para erros de conversão de arquivo
 */
public class FileConversionException extends RuntimeException {
    
    public FileConversionException(String message) {
        super(message);
    }
    
    public FileConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
