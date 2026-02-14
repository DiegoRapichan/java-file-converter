package com.fileconverter.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.colors.ColorConstants;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Conversor de JSON para PDF
 * Converte arrays JSON em documentos PDF formatados como tabelas
 */
@Component
public class JsonToPdfConverter implements FileConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws Exception {
        List<Map<String, Object>> records = objectMapper.readValue(inputStream, List.class);
        
        if (records.isEmpty()) {
            throw new IllegalArgumentException("JSON array is empty");
        }
        
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Título
        document.add(new Paragraph("JSON Data Report")
                .setFontSize(18)
                .setBold()
                .setMarginBottom(20));
        
        // Cria tabela com base nas keys do primeiro objeto
        Map<String, Object> firstRecord = records.get(0);
        int numColumns = firstRecord.size();
        Table table = new Table(numColumns);
        table.setWidth(500);
        
        // Headers
        for (String key : firstRecord.keySet()) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(key).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addHeaderCell(headerCell);
        }
        
        // Data rows
        for (Map<String, Object> record : records) {
            for (Object value : record.values()) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(value))));
            }
        }
        
        document.add(table);
        document.close();
    }

    @Override
    public String getConversionType() {
        return "JSON_TO_PDF";
    }
}
