package com.fileconverter.converter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface base para todos os conversores de arquivo
 * Implementa o padrão Strategy
 */
public interface FileConverter {
    
    /**
     * Converte um arquivo de entrada para o formato de saída
     * 
     * @param inputStream Stream de entrada com o arquivo original
     * @param outputStream Stream de saída para o arquivo convertido
     * @throws Exception se houver erro na conversão
     */
    void convert(InputStream inputStream, OutputStream outputStream) throws Exception;
    
    /**
     * Retorna o tipo de conversão suportado
     * 
     * @return ConversionType
     */
    String getConversionType();
}
