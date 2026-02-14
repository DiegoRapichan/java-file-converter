import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, timeout, catchError, of } from 'rxjs';
import { ConversionResponse, ConversionType, ConversionTypeInfo } from '../models/conversion.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FileConversionService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /**
   * Obtém todos os tipos de conversão suportados
   */
  getSupportedConversions(): Observable<ConversionTypeInfo[]> {
    return this.http.get<ConversionTypeInfo[]>(`${this.apiUrl}/convert/types`).pipe(
      timeout(3000), // 3 segundos de timeout
      catchError(error => {
        console.warn('API não disponível:', error.message);
        return of([]); // Retorna array vazio em caso de erro
      })
    );
  }

  /**
   * Converte um arquivo para o formato especificado
   */
  convertFile(file: File, conversionType: ConversionType): Observable<ConversionResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('conversionType', conversionType);

    return this.http.post<ConversionResponse>(`${this.apiUrl}/convert/upload`, formData);
  }

  /**
   * Faz download do arquivo convertido
   */
  downloadFile(fileName: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/convert/download/${fileName}`, {
      responseType: 'blob'
    });
  }

  /**
   * Verifica se a API está online
   */
  healthCheck(): Observable<any> {
    return this.http.get(`${this.apiUrl}/convert/health`);
  }
}
