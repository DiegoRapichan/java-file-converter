package com.fileconverter.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * Conversor de JSON para CSV
 * Converte arrays JSON em formato CSV com cabeçalhos
 */
@Component
public class JsonToCsvConverter implements FileConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws Exception {
        // Lê o JSON como lista de mapas
        List<Map<String, Object>> records = objectMapper.readValue(inputStream, List.class);
        
        if (records.isEmpty()) {
            throw new IllegalArgumentException("JSON array is empty");
        }
        
        // Extrai os headers (keys do primeiro objeto)
        Set<String> headers = new LinkedHashSet<>(records.get(0).keySet());
        
        try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])))) {
            
            for (Map<String, Object> record : records) {
                for (String header : headers) {
                    csvPrinter.print(record.get(header));
                }
                csvPrinter.println();
            }
            
            csvPrinter.flush();
        }
    }

    @Override
    public String getConversionType() {
        return "JSON_TO_CSV";
    }
}
