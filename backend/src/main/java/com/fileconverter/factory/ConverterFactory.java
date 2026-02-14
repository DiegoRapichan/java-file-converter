package com.fileconverter.factory;

import com.fileconverter.converter.*;
import com.fileconverter.model.ConversionType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory para criar conversores específicos baseado no tipo de conversão
 * Implementa o padrão Factory Method
 */
@Component
public class ConverterFactory {

    private final Map<String, FileConverter> converters = new HashMap<>();

    public ConverterFactory(
            CsvToJsonConverter csvToJsonConverter,
            JsonToCsvConverter jsonToCsvConverter,
            JsonToXmlConverter jsonToXmlConverter,
            XmlToJsonConverter xmlToJsonConverter,
            CsvToExcelConverter csvToExcelConverter,
            TextToPdfConverter textToPdfConverter,
            JsonToPdfConverter jsonToPdfConverter
    ) {
        converters.put("CSV_TO_JSON", csvToJsonConverter);
        converters.put("JSON_TO_CSV", jsonToCsvConverter);
        converters.put("JSON_TO_XML", jsonToXmlConverter);
        converters.put("XML_TO_JSON", xmlToJsonConverter);
        converters.put("CSV_TO_EXCEL", csvToExcelConverter);
        converters.put("TEXT_TO_PDF", textToPdfConverter);
        converters.put("JSON_TO_PDF", jsonToPdfConverter);
    }

    /**
     * Retorna o conversor apropriado para o tipo de conversão
     * 
     * @param conversionType Tipo de conversão desejado
     * @return FileConverter correspondente
     * @throws IllegalArgumentException se o tipo não for suportado
     */
    public FileConverter getConverter(ConversionType conversionType) {
        FileConverter converter = converters.get(conversionType.name());
        if (converter == null) {
            throw new IllegalArgumentException("Unsupported conversion type: " + conversionType);
        }
        return converter;
    }

    /**
     * Verifica se um tipo de conversão é suportado
     * 
     * @param conversionType Tipo de conversão
     * @return true se suportado, false caso contrário
     */
    public boolean isSupported(ConversionType conversionType) {
        return converters.containsKey(conversionType.name());
    }
}
