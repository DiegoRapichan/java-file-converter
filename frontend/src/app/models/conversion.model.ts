// Tipos de conversão suportados
export enum ConversionType {
  CSV_TO_JSON = 'CSV_TO_JSON',
  JSON_TO_CSV = 'JSON_TO_CSV',
  JSON_TO_XML = 'JSON_TO_XML',
  XML_TO_JSON = 'XML_TO_JSON',
  CSV_TO_EXCEL = 'CSV_TO_EXCEL',
  TEXT_TO_PDF = 'TEXT_TO_PDF',
  JSON_TO_PDF = 'JSON_TO_PDF'
}

// Interface para resposta de conversão
export interface ConversionResponse {
  success: boolean;
  message: string;
  originalFileName?: string;
  convertedFileName?: string;
  conversionType?: ConversionType;
  fileSizeBytes?: number;
  downloadUrl?: string;
  errorDetails?: string;
}

// Interface para tipo de conversão
export interface ConversionTypeInfo {
  type: string;
  description: string;
}

// Mapeamento de descrições
export const CONVERSION_DESCRIPTIONS: { [key: string]: string } = {
  CSV_TO_JSON: 'CSV para JSON',
  JSON_TO_CSV: 'JSON para CSV',
  JSON_TO_XML: 'JSON para XML',
  XML_TO_JSON: 'XML para JSON',
  CSV_TO_EXCEL: 'CSV para Excel',
  TEXT_TO_PDF: 'Texto para PDF',
  JSON_TO_PDF: 'JSON para PDF'
};
