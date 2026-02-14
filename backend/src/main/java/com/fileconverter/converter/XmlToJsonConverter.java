package com.fileconverter.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Conversor de XML para JSON
 * Converte documentos XML em formato JSON
 */
@Component
public class XmlToJsonConverter implements FileConverter {

    private final XmlMapper xmlMapper = new XmlMapper();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws Exception {
        // Lê o XML como objeto genérico
        Object data = xmlMapper.readValue(inputStream, Object.class);
        
        // Converte para JSON formatado
        String json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        
        outputStream.write(json.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getConversionType() {
        return "XML_TO_JSON";
    }
}
