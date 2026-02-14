package com.fileconverter.model;

/**
 * Tipos de conversão suportados pela API
 */
public enum ConversionType {
    CSV_TO_JSON("CSV para JSON"),
    JSON_TO_CSV("JSON para CSV"),
    JSON_TO_XML("JSON para XML"),
    XML_TO_JSON("XML para JSON"),
    CSV_TO_EXCEL("CSV para Excel"),
    JSON_TO_PDF("JSON para PDF"),
    TEXT_TO_PDF("Texto para PDF");

    private final String description;

    ConversionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
