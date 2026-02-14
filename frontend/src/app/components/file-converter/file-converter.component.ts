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

  // Expor enum para o template
  ConversionType = ConversionType;
  conversionDescriptions = CONVERSION_DESCRIPTIONS;

  constructor(private fileConversionService: FileConversionService) {}

  ngOnInit(): void {
    this.loadConversionTypes();
  }

  /**
   * Carrega os tipos de conversão suportados
   */
  loadConversionTypes(): void {
    this.fileConversionService.getSupportedConversions().subscribe({
      next: (types) => {
        this.conversionTypes = types;
      },
      error: (error) => {
        console.error('Error loading conversion types', error);
        this.errorMessage = 'Erro ao carregar tipos de conversão';
      }
    });
  }

  /**
   * Manipula seleção de arquivo
   */
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.errorMessage = null;
      this.conversionResult = null;
    }
  }

  /**
   * Realiza a conversão do arquivo
   */
  convertFile(): void {
    if (!this.selectedFile) {
      this.errorMessage = 'Por favor, selecione um arquivo';
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
}
