package com.fileconverter.converter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Conversor de CSV para Excel (.xlsx)
 * Converte arquivos CSV em planilhas Excel formatadas
 */
@Component
public class CsvToExcelConverter implements FileConverter {

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws Exception {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim());
             Workbook workbook = new XSSFWorkbook()) {
            
            Sheet sheet = workbook.createSheet("Data");
            
            // Cria estilo para o cabeçalho
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Adiciona cabeçalhos
            Row headerRow = sheet.createRow(0);
            List<String> headers = csvParser.getHeaderNames();
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }
            
            // Adiciona dados
            int rowNum = 1;
            for (CSVRecord csvRecord : csvParser) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(csvRecord.get(i));
                }
            }
            
            // Auto-ajusta largura das colunas
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
        }
    }

    @Override
    public String getConversionType() {
        return "CSV_TO_EXCEL";
    }
}
