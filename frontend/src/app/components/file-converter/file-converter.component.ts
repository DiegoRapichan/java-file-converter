import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FileConversionService } from '../../services/file-conversion.service';
import { ConversionType, ConversionResponse, ConversionTypeInfo, CONVERSION_DESCRIPTIONS } from '../../models/conversion.model';

@Component({
  selector: 'app-file-converter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './file-converter.component.html',
  styleUrls: ['./file-converter.component.css']
})
export class FileConverterComponent implements OnInit {
  selectedFile: File | null = null;
  selectedConversionType: ConversionType = ConversionType.CSV_TO_JSON;
  conversionTypes: ConversionTypeInfo[] = [];
  isLoading = false;
  conversionResult: ConversionResponse | null = null;
  errorMessage: string | null = null;
  filePreview: string | null = null;
  maxFileSize = 10 * 1024 * 1024; // 10MB

  // Expor enum para o template
  ConversionType = ConversionType;
  conversionDescriptions = CONVERSION_DESCRIPTIONS;

  constructor(private fileConversionService: FileConversionService) {}

  ngOnInit(): void {
    // Carrega lista padrão PRIMEIRO
    this.conversionTypes = [
      { type: 'CSV_TO_JSON', description: 'CSV para JSON' },
      { type: 'JSON_TO_CSV', description: 'JSON para CSV' },
      { type: 'JSON_TO_XML', description: 'JSON para XML' },
      { type: 'XML_TO_JSON', description: 'XML para JSON' },
      { type: 'CSV_TO_EXCEL', description: 'CSV para Excel' },
      { type: 'TEXT_TO_PDF', description: 'Texto para PDF' },
      { type: 'JSON_TO_PDF', description: 'JSON para PDF' }
    ];
    
    // Tenta carregar da API (se backend estiver rodando)
    this.loadConversionTypes();
  }

  /**
   * Carrega os tipos de conversão suportados da API (se disponível)
   */
  loadConversionTypes(): void {
    this.fileConversionService.getSupportedConversions().subscribe({
      next: (types) => {
        if (types && types.length > 0) {
          this.conversionTypes = types;
          console.log('✅ Tipos carregados da API:', types);
        }
      },
      error: (error) => {
        console.warn('⚠️ Backend não disponível, usando lista padrão');
        console.error('Erro:', error.message);
        // Lista já foi carregada no ngOnInit, não faz nada
      }
    });
  }

  /**
   * Manipula seleção de arquivo
   */
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Validação de tamanho
      if (file.size > this.maxFileSize) {
        this.errorMessage = `⚠️ Arquivo muito grande! Tamanho máximo: ${this.formatFileSize(this.maxFileSize)}. Arquivo selecionado: ${this.formatFileSize(file.size)}`;
        this.selectedFile = null;
        this.filePreview = null;
        return;
      }

      this.selectedFile = file;
      this.errorMessage = null;
      this.conversionResult = null;
      
      // Gerar preview do arquivo
      this.generateFilePreview(file);
    }
  }

  /**
   * Gera preview do conteúdo do arquivo
   */
  generateFilePreview(file: File): void {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      const content = e.target.result;
      // Limita preview a 500 caracteres
      this.filePreview = content.length > 500 
        ? content.substring(0, 500) + '...' 
        : content;
    };
    reader.readAsText(file);
  }

  /**
   * Realiza a conversão do arquivo
   */
  convertFile(): void {
    if (!this.selectedFile) {
      this.errorMessage = 'Por favor, selecione um arquivo';
      return;
    }

    // VALIDAÇÃO DE FORMATO
    const validationError = this.validateFileFormat();
    if (validationError) {
      this.errorMessage = validationError;
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.conversionResult = null;

    this.fileConversionService.convertFile(this.selectedFile, this.selectedConversionType).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.conversionResult = response;
        if (response.success) {
          this.errorMessage = null;
        } else {
          this.errorMessage = response.errorDetails || 'Erro na conversão';
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Erro ao converter arquivo';
        console.error('Conversion error', error);
      }
    });
  }

  /**
   * Valida se o formato do arquivo é compatível com a conversão selecionada
   */
  validateFileFormat(): string | null {
    if (!this.selectedFile) return null;

    const fileName = this.selectedFile.name.toLowerCase();
    const fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);

    // Mapeamento de conversões e formatos esperados
    const formatValidation: { [key: string]: { expected: string[], description: string } } = {
      'CSV_TO_JSON': { 
        expected: ['csv'], 
        description: 'arquivo CSV (.csv)' 
      },
      'JSON_TO_CSV': { 
        expected: ['json'], 
        description: 'arquivo JSON (.json)' 
      },
      'JSON_TO_XML': { 
        expected: ['json'], 
        description: 'arquivo JSON (.json)' 
      },
      'XML_TO_JSON': { 
        expected: ['xml'], 
        description: 'arquivo XML (.xml)' 
      },
      'CSV_TO_EXCEL': { 
        expected: ['csv'], 
        description: 'arquivo CSV (.csv)' 
      },
      'TEXT_TO_PDF': { 
        expected: ['txt', 'text'], 
        description: 'arquivo de texto (.txt)' 
      },
      'JSON_TO_PDF': { 
        expected: ['json'], 
        description: 'arquivo JSON (.json)' 
      }
    };

    const validation = formatValidation[this.selectedConversionType];
    
    if (validation && !validation.expected.includes(fileExtension)) {
      return `⚠️ Formato incorreto! Para "${this.conversionDescriptions[this.selectedConversionType]}", você precisa selecionar um ${validation.description}. Arquivo selecionado: .${fileExtension}`;
    }

    // Validação de conteúdo
    if (this.filePreview) {
      const contentError = this.validateFileContent(fileExtension);
      if (contentError) return contentError;
    }

    return null;
  }

  /**
   * Valida o conteúdo do arquivo
   */
  validateFileContent(extension: string): string | null {
    if (!this.filePreview) return null;

    try {
      // Validar JSON
      if (extension === 'json') {
        JSON.parse(this.filePreview.length > 500 ? this.filePreview.substring(0, 500) : this.filePreview);
      }
      
      // Validar XML (básico)
      if (extension === 'xml') {
        if (!this.filePreview.trim().startsWith('<')) {
          return '⚠️ Arquivo XML inválido! O arquivo não parece ser um XML válido.';
        }
      }

      // Validar CSV (básico)
      if (extension === 'csv') {
        const lines = this.filePreview.split('\n');
        if (lines.length < 2) {
          return '⚠️ Arquivo CSV inválido! O arquivo deve ter pelo menos um cabeçalho e uma linha de dados.';
        }
      }

      return null;
    } catch (error) {
      if (extension === 'json') {
        return '⚠️ Arquivo JSON inválido! Verifique a sintaxe do arquivo.';
      }
      return null;
    }
  }

  /**
   * Faz download do arquivo convertido
   */
  downloadConvertedFile(): void {
    if (!this.conversionResult || !this.conversionResult.convertedFileName) {
      return;
    }

    this.fileConversionService.downloadFile(this.conversionResult.convertedFileName).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = this.conversionResult!.convertedFileName!;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Download error', error);
        this.errorMessage = 'Erro ao fazer download do arquivo';
      }
    });
  }

  /**
   * Reseta o formulário
   */
  reset(): void {
    this.selectedFile = null;
    this.conversionResult = null;
    this.errorMessage = null;
    this.filePreview = null;
    this.selectedConversionType = ConversionType.CSV_TO_JSON;
  }

  /**
   * Formata tamanho do arquivo em formato legível
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }

  /**
   * Retorna o formato esperado para o tipo de conversão selecionado
   */
  getExpectedFormat(): string {
    const formatMap: { [key: string]: string } = {
      'CSV_TO_JSON': '.csv',
      'JSON_TO_CSV': '.json',
      'JSON_TO_XML': '.json',
      'XML_TO_JSON': '.xml',
      'CSV_TO_EXCEL': '.csv',
      'TEXT_TO_PDF': '.txt',
      'JSON_TO_PDF': '.json'
    };
    return formatMap[this.selectedConversionType] || '';
  }
}
