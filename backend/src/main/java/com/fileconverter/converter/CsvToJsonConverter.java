package com.fileconverter.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Conversor de CSV para JSON
 * Converte arquivos CSV em formato JSON array de objetos
 */
@Component
public class CsvToJsonConverter implements FileConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws Exception {
        List<Map<String, String>> records = new ArrayList<>();
        
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {
            
            for (CSVRecord csvRecord : csvParser) {
                Map<String, String> record = new LinkedHashMap<>();
                csvRecord.toMap().forEach(record::put);
                records.add(record);
            }
        }
        
        // Escreve o JSON formatado
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
        outputStream.write(json.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getConversionType() {
        return "CSV_TO_JSON";
    }
}
