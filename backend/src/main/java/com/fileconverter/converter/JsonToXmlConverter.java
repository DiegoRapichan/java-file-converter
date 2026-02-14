package com.fileconverter.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Conversor de JSON para XML
 * Converte objetos JSON em formato XML
 */
@Component
public class JsonToXmlConverter implements FileConverter {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws Exception {
        // Lê o JSON como objeto genérico
        Object data = jsonMapper.readValue(inputStream, Object.class);
        
        // Converte para XML formatado
        String xml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        
        outputStream.write(xml.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getConversionType() {
        return "JSON_TO_XML";
    }
}
