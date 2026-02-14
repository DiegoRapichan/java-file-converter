package com.fileconverter.converter;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Conversor de Texto para PDF
 * Converte arquivos de texto simples em documentos PDF
 */
@Component
public class TextToPdfConverter implements FileConverter {

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws Exception {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                document.add(new Paragraph(line));
            }
        }
        
        document.close();
    }

    @Override
    public String getConversionType() {
        return "TEXT_TO_PDF";
    }
}
