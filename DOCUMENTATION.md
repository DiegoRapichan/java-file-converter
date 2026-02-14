# 📘 DOCUMENTAÇÃO TÉCNICA COMPLETA - File Converter

## 📑 Índice

1. [Visão Geral da Arquitetura](#visão-geral-da-arquitetura)
2. [Backend - Explicação Detalhada](#backend---explicação-detalhada)
3. [Frontend - Explicação Detalhada](#frontend---explicação-detalhada)
4. [Guia de Deploy](#guia-de-deploy)
5. [Troubleshooting](#troubleshooting)
6. [Boas Práticas](#boas-práticas)

---

## 🏗️ Visão Geral da Arquitetura

### Fluxo Completo da Aplicação

```
┌──────────────┐      HTTP Request       ┌──────────────┐
│   Angular    │ ───────────────────────> │ Spring Boot  │
│   Frontend   │                          │   Backend    │
│              │ <─────────────────────── │              │
│  Port 4200   │      HTTP Response       │  Port 8080   │
└──────────────┘                          └──────────────┘
       │                                          │
       │                                          │
       ▼                                          ▼
  Components                                  Controllers
  Services                                    Services
  Models                                      Converters
                                             Factory Pattern
```

---

## 🔧 Backend - Explicação Detalhada

### 1. FileConverterApplication.java

**O que é:** Classe principal do Spring Boot - Entry point da aplicação.

**O que faz:**
- Inicializa o container Spring
- Habilita auto-configuração do Spring Boot
- Carrega todos os beans (componentes) da aplicação

**Por que é importante:**
- Sem esta classe, a aplicação não inicia
- A anotação `@SpringBootApplication` combina 3 anotações:
  - `@Configuration`: Indica que é uma classe de configuração
  - `@EnableAutoConfiguration`: Spring Boot configura automaticamente
  - `@ComponentScan`: Escaneia pacotes em busca de componentes

**Como funciona:**
```java
@SpringBootApplication  // ← Esta anotação faz "a mágica"
public class FileConverterApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileConverterApplication.class, args);
        // ↑ Este método inicia todo o ecossistema Spring
    }
}
```

---

### 2. ConversionType.java (Enum)

**O que é:** Enumeração que define todos os tipos de conversão suportados.

**O que faz:**
- Define constantes para cada tipo de conversão
- Armazena descrição amigável para cada tipo

**Por que é importante:**
- **Type Safety**: Previne erros de digitação
- **Auto-complete**: IDEs sugerem opções válidas
- **Documentação**: Descrições claras de cada tipo

**Como usar:**
```java
ConversionType type = ConversionType.CSV_TO_JSON;
String desc = type.getDescription(); // "CSV para JSON"
```

---

### 3. ConversionResponse.java (DTO)

**O que é:** Data Transfer Object - Objeto que transporta dados entre camadas.

**O que faz:**
- Encapsula informações sobre o resultado da conversão
- Padroniza a estrutura de resposta da API

**Por que usar Lombok:**
```java
@Data                    // Gera getters, setters, toString, equals, hashCode
@Builder                 // Padrão Builder para criar objetos
@NoArgsConstructor      // Construtor sem argumentos
@AllArgsConstructor     // Construtor com todos os argumentos
```

**Benefícios:**
- Reduz 80% do código boilerplate
- Código mais limpo e legível
- Fácil de criar objetos complexos

**Exemplo de uso:**
```java
ConversionResponse response = ConversionResponse.builder()
    .success(true)
    .message("Conversão bem-sucedida")
    .originalFileName("data.csv")
    .convertedFileName("data_123.json")
    .build();
```

---

### 4. FileConverter.java (Interface)

**O que é:** Interface que define o contrato que todos os conversores devem seguir.

**O que faz:**
- Define método `convert()` que todos os conversores implementam
- Garante consistência entre diferentes conversores

**Por que usar uma interface (Strategy Pattern):**
- **Flexibilidade**: Fácil adicionar novos conversores
- **Testabilidade**: Fácil fazer mocks para testes
- **SOLID**: Princípio da Inversão de Dependência

**Como funciona:**
```java
public interface FileConverter {
    void convert(InputStream input, OutputStream output) throws Exception;
    String getConversionType();
}
```

Cada conversor implementa esta interface de forma diferente, mas todos seguem o mesmo padrão.

---

### 5. Conversores Específicos (7 classes)

#### 5.1. CsvToJsonConverter

**O que faz:**
1. Lê arquivo CSV linha por linha
2. Primeira linha = headers (nomes das colunas)
3. Demais linhas = dados
4. Converte cada linha em um objeto JSON
5. Retorna array de objetos

**Bibliotecas usadas:**
- **Apache Commons CSV**: Parsing robusto de CSV
- **Jackson**: Serialização para JSON

**Exemplo:**
```
CSV:              JSON:
name,age          [
John,30            {
Jane,25              "name": "John",
                     "age": "30"
                   },
                   {
                     "name": "Jane",
                     "age": "25"
                   }
                 ]
```

#### 5.2. JsonToCsvConverter

**O que faz:**
1. Lê JSON como lista de mapas
2. Extrai headers das keys do primeiro objeto
3. Escreve headers no CSV
4. Escreve dados linha por linha

**Tratamento de erros:**
- Valida se JSON não está vazio
- Garante que todos os objetos têm as mesmas keys

#### 5.3. JsonToXmlConverter e XmlToJsonConverter

**O que fazem:**
- Usam **Jackson XML** para converter entre formatos
- XmlMapper lê/escreve XML
- ObjectMapper lê/escreve JSON

**Importante:** Preservam estrutura hierárquica dos dados

#### 5.4. CsvToExcelConverter

**O que faz:**
1. Cria workbook Excel (.xlsx)
2. Cria sheet "Data"
3. Formata headers (negrito, fundo cinza)
4. Preenche dados
5. Auto-ajusta largura das colunas

**Bibliotecas usadas:**
- **Apache POI**: Manipulação de arquivos Office

**Features:**
- Headers formatados (bold + background)
- Auto-size de colunas
- Suporte para múltiplas sheets (extensível)

#### 5.5. TextToPdfConverter

**O que faz:**
1. Cria documento PDF
2. Lê texto linha por linha
3. Adiciona cada linha como parágrafo no PDF

**Bibliotecas usadas:**
- **iText 7**: Geração de PDF

**Simples mas efetivo** para converter .txt → .pdf

#### 5.6. JsonToPdfConverter

**O que faz:**
1. Lê JSON como lista de objetos
2. Cria tabela PDF
3. Headers da tabela = keys do JSON
4. Linhas da tabela = valores
5. Formata com cores e estilos

**Features avançadas:**
- Título do documento
- Headers com fundo cinza
- Bordas nas células
- Formatação profissional

---

### 6. ConverterFactory.java (Factory Pattern)

**O que é:** Classe que cria e gerencia instâncias de conversores.

**Por que usar Factory Pattern:**
- **Centralização**: Um único ponto para obter conversores
- **Flexibilidade**: Fácil adicionar novos conversores
- **Encapsulamento**: Cliente não precisa saber qual classe instanciar

**Como funciona:**
```java
@Component
public class ConverterFactory {
    private final Map<String, FileConverter> converters = new HashMap<>();
    
    // Spring injeta todos os conversores automaticamente
    public ConverterFactory(
        CsvToJsonConverter csvToJson,
        JsonToCsvConverter jsonToCsv,
        // ... outros conversores
    ) {
        converters.put("CSV_TO_JSON", csvToJson);
        converters.put("JSON_TO_CSV", jsonToCsv);
        // ...
    }
    
    public FileConverter getConverter(ConversionType type) {
        return converters.get(type.name());
    }
}
```

**Benefícios:**
- Código do controller fica simples
- Fácil fazer testes unitários
- Extensível sem modificar código existente (Open/Closed Principle)

---

### 7. FileConversionService.java

**O que é:** Camada de lógica de negócio - orquestra todo o processo de conversão.

**Responsabilidades:**
1. **Validar** arquivo de entrada
2. **Obter** conversor correto via Factory
3. **Executar** conversão
4. **Gerenciar** arquivos de saída
5. **Criar** resposta para o cliente

**Fluxo de execução:**
```
1. Recebe arquivo + tipo de conversão
2. Valida se arquivo não está vazio
3. Factory retorna conversor específico
4. Gera nome único para arquivo de saída (UUID)
5. Executa conversão
6. Salva em /output/
7. Retorna ConversionResponse com detalhes
```

**Tratamento de erros:**
- Try-catch captura qualquer exceção
- Retorna resposta com `success: false`
- Inclui detalhes do erro para debugging

**Geração de nome único:**
```java
String generateOutputFileName(String original, ConversionType type) {
    String baseName = original.substring(0, original.lastIndexOf('.'));
    String extension = getOutputExtension(type);
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    return baseName + "_" + uniqueId + "." + extension;
}
```

Exemplo: `data.csv` → `data_a1b2c3d4.json`

---

### 8. FileConverterController.java (REST API)

**O que é:** Controlador REST - expõe endpoints HTTP.

**Anotações importantes:**
```java
@RestController              // Indica que é um controller REST
@RequestMapping("/api/convert") // Prefixo de todas as rotas
@CrossOrigin(origins = "*")  // Permite requisições do frontend
```

**Endpoints explicados:**

#### GET /api/convert/types
```java
@GetMapping("/types")
public ResponseEntity<List<ConversionTypeDTO>> getSupportedConversions()
```
- **O que retorna:** Lista de todos os tipos de conversão
- **Usado para:** Popular dropdown no frontend
- **Formato:** `[{type: "CSV_TO_JSON", description: "CSV para JSON"}, ...]`

#### POST /api/convert/upload
```java
@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ConversionResponse> convertFile(
    @RequestParam("file") MultipartFile file,
    @RequestParam("conversionType") ConversionType conversionType
)
```
- **O que faz:** Recebe arquivo e converte
- **Content-Type:** multipart/form-data (para upload de arquivos)
- **Validações:** Verifica se arquivo não está vazio
- **Retorna:** ConversionResponse com detalhes ou erro

#### GET /api/convert/download/{fileName}
```java
@GetMapping("/download/{fileName}")
public ResponseEntity<Resource> downloadFile(@PathVariable String fileName)
```
- **O que faz:** Retorna arquivo convertido para download
- **Content-Type:** application/octet-stream (download binário)
- **Header:** Content-Disposition com nome do arquivo
- **Tratamento:** FileNotFoundException se arquivo não existir

#### GET /api/convert/health
```java
@GetMapping("/health")
public ResponseEntity<HealthResponse> healthCheck()
```
- **O que faz:** Verifica se API está online
- **Usado para:** Monitoramento e health checks
- **Sempre retorna:** 200 OK se aplicação está rodando

---

### 9. Exception Handlers

#### GlobalExceptionHandler.java

**O que é:** Manipulador global de exceções - captura erros e retorna respostas padronizadas.

**Anotação:** `@ControllerAdvice` - aplica-se a todos os controllers

**Exceções tratadas:**

1. **FileConversionException** (customizada)
   - Status: 400 Bad Request
   - Quando: Erro durante conversão

2. **FileNotFoundException**
   - Status: 404 Not Found
   - Quando: Arquivo para download não existe

3. **MaxUploadSizeExceededException**
   - Status: 413 Payload Too Large
   - Quando: Arquivo maior que 10MB

4. **IllegalArgumentException**
   - Status: 400 Bad Request
   - Quando: Argumento inválido (ex: tipo de conversão desconhecido)

5. **Exception** (genérica)
   - Status: 500 Internal Server Error
   - Quando: Erro inesperado

**Benefícios:**
- Respostas consistentes
- Logs centralizados
- Códigos HTTP corretos
- Mensagens claras para o cliente

---

### 10. Configurações

#### CorsConfig.java

**O que é:** Configuração de CORS (Cross-Origin Resource Sharing).

**Por que é necessário:**
- Backend (8080) e Frontend (4200) estão em portas diferentes
- Navegadores bloqueiam requisições cross-origin por padrão
- CORS permite essa comunicação

**Configuração:**
```java
.allowedOrigins("http://localhost:4200")  // Frontend
.allowedMethods("GET", "POST", "PUT", "DELETE")
.allowedHeaders("*")
.allowCredentials(true)
```

**⚠️ Em produção:** Trocar `"*"` por domínio específico do frontend

#### OpenApiConfig.java

**O que é:** Configuração do Swagger/OpenAPI - documentação interativa da API.

**O que cria:**
- Página Swagger UI em `/swagger-ui.html`
- Especificação OpenAPI em `/api-docs`
- Interface para testar endpoints

**Metadados configurados:**
- Título da API
- Versão
- Descrição
- Informações de contato
- Licença

**Benefícios:**
- Documentação sempre atualizada
- Interface para testar API sem Postman
- Geração de clients automática

#### application.properties

**Configurações principais:**

```properties
# Porta da aplicação
server.port=8080

# Upload de arquivos
spring.servlet.multipart.max-file-size=10MB     # Máximo por arquivo
spring.servlet.multipart.max-request-size=10MB  # Máximo por requisição

# Logs
logging.level.root=INFO                         # Nível geral
logging.level.com.fileconverter=DEBUG          # Logs da aplicação

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html     # URL do Swagger
```

---

### 11. pom.xml (Dependências Maven)

**Dependências explicadas:**

#### Spring Boot Starters
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
- **O que traz:** Tomcat embedded, Spring MVC, Jackson
- **Para que:** Criar REST APIs

#### Apache Commons CSV
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-csv</artifactId>
    <version>1.10.0</version>
</dependency>
```
- **Para que:** Ler/escrever arquivos CSV
- **Vantagem:** Robusto, lida com edge cases (vírgulas em valores, etc.)

#### Jackson
```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```
- **Para que:** Converter JSON ↔ XML
- **Vantagem:** Mesma biblioteca para ambos os formatos

#### Apache POI
```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```
- **Para que:** Criar/ler arquivos Excel (.xlsx)
- **OOXML:** Formato moderno do Excel (xlsx, não xls)

#### iText
```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
    <type>pom</type>
</dependency>
```
- **Para que:** Criar PDFs
- **Versão 7:** Mais moderna que iText 5
- **Type pom:** Importa todos os módulos necessários

#### SpringDoc OpenAPI
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```
- **Para que:** Gerar Swagger UI automaticamente
- **Como:** Escaneia annotations e gera documentação

---

## 🎨 Frontend - Explicação Detalhada

### 1. Estrutura Angular

#### package.json

**Dependências principais:**
- `@angular/core`: Framework Angular
- `@angular/common`: Funcionalidades comuns (NgIf, NgFor)
- `@angular/forms`: FormsModule para [(ngModel)]
- `rxjs`: Programação reativa (Observables)

**Scripts:**
- `npm start`: Inicia servidor de desenvolvimento
- `npm run build`: Build de produção
- `ng serve`: Alias para start

---

### 2. Models (TypeScript)

#### conversion.model.ts

**Interfaces TypeScript:**
```typescript
export interface ConversionResponse {
  success: boolean;
  message: string;
  originalFileName?: string;  // ? = opcional
  convertedFileName?: string;
  // ...
}
```

**Por que usar interfaces:**
- **Type safety**: Evita erros de digitação
- **IntelliSense**: Auto-complete no VS Code
- **Documentação**: Estrutura clara dos dados

**Enum:**
```typescript
export enum ConversionType {
  CSV_TO_JSON = 'CSV_TO_JSON',
  JSON_TO_CSV = 'JSON_TO_CSV',
  // ...
}
```

**Benefícios:**
- String constants tipadas
- Previne magic strings no código
- Refactoring seguro

---

### 3. Services

#### file-conversion.service.ts

**O que é:** Service Angular que centraliza chamadas HTTP.

**Decorador:** `@Injectable({ providedIn: 'root' })` - Singleton global

**Métodos explicados:**

##### getSupportedConversions()
```typescript
getSupportedConversions(): Observable<ConversionTypeInfo[]> {
  return this.http.get<ConversionTypeInfo[]>(`${this.apiUrl}/convert/types`);
}
```
- **Retorna:** Observable (stream reativo)
- **Tipo:** Array de ConversionTypeInfo
- **Usado:** Carregar dropdown no componente

##### convertFile()
```typescript
convertFile(file: File, conversionType: ConversionType): Observable<ConversionResponse> {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('conversionType', conversionType);
  
  return this.http.post<ConversionResponse>(`${this.apiUrl}/convert/upload`, formData);
}
```
- **FormData:** Necessário para upload de arquivos
- **Content-Type:** Automaticamente multipart/form-data
- **Observable:** Permite subscribe no componente

##### downloadFile()
```typescript
downloadFile(fileName: string): Observable<Blob> {
  return this.http.get(`${this.apiUrl}/convert/download/${fileName}`, {
    responseType: 'blob'  // ← Importante: arquivo binário
  });
}
```
- **Blob:** Binary Large Object (arquivo binário)
- **responseType:** Diz ao HttpClient que não é JSON

---

### 4. Components

#### file-converter.component.ts

**Decoradores:**
```typescript
@Component({
  selector: 'app-file-converter',      // Tag HTML
  standalone: true,                     // Componente standalone (Angular 17+)
  imports: [CommonModule, FormsModule], // Módulos necessários
  templateUrl: './file-converter.component.html',
  styleUrls: ['./file-converter.component.css']
})
```

**Propriedades do componente:**

```typescript
selectedFile: File | null = null;              // Arquivo selecionado
selectedConversionType: ConversionType = ...;  // Tipo de conversão
isLoading = false;                             // Estado de loading
conversionResult: ConversionResponse | null;   // Resultado
errorMessage: string | null;                   // Mensagem de erro
```

**Lifecycle hook:**
```typescript
ngOnInit(): void {
  this.loadConversionTypes();  // Carrega tipos ao iniciar
}
```

**Métodos explicados:**

##### onFileSelected()
```typescript
onFileSelected(event: any): void {
  const file = event.target.files[0];
  if (file) {
    this.selectedFile = file;
    this.errorMessage = null;      // Limpa erros anteriores
    this.conversionResult = null;  // Limpa resultado anterior
  }
}
```
- **event.target.files[0]:** Primeiro arquivo selecionado
- **Reseta estado:** Limpa erros e resultados anteriores

##### convertFile()
```typescript
convertFile(): void {
  if (!this.selectedFile) {
    this.errorMessage = 'Por favor, selecione um arquivo';
    return;
  }
  
  this.isLoading = true;  // Mostra loading
  
  this.fileConversionService.convertFile(this.selectedFile, this.selectedConversionType)
    .subscribe({
      next: (response) => {
        this.isLoading = false;
        this.conversionResult = response;
        // Trata sucesso/erro baseado em response.success
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Erro ao converter';
      }
    });
}
```
- **Validação:** Verifica se arquivo foi selecionado
- **Loading state:** UX melhor
- **Subscribe:** next (sucesso) e error (erro)
- **Encadeamento opcional:** Usa optional chaining (`?.`)

##### downloadConvertedFile()
```typescript
downloadConvertedFile(): void {
  this.fileConversionService.downloadFile(this.conversionResult!.convertedFileName!)
    .subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);  // Cria URL temporária
        const link = document.createElement('a');       // Cria link
        link.href = url;
        link.download = this.conversionResult!.convertedFileName!;
        link.click();                                   // Trigger download
        window.URL.revokeObjectURL(url);               // Libera memória
      },
      error: (error) => {
        this.errorMessage = 'Erro ao fazer download';
      }
    });
}
```
- **createObjectURL:** Cria URL blob temporária
- **Programmatic click:** Trigger download
- **revokeObjectURL:** Importante para não vazar memória

##### formatFileSize()
```typescript
formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}
```
- **Converte bytes:** Para formato legível (KB, MB)
- **Math.log:** Calcula a escala apropriada
- **Arredondamento:** 2 casas decimais

---

#### file-converter.component.html (Template)

**Diretivas Angular importantes:**

##### *ngIf
```html
<p class="file-info" *ngIf="selectedFile">
  Tamanho: {{ formatFileSize(selectedFile.size) }}
</p>
```
- **Renderização condicional:** Só mostra se selectedFile existe

##### *ngFor
```html
<option *ngFor="let type of conversionTypes" [value]="type.type">
  {{ type.description }}
</option>
```
- **Loop:** Itera sobre array conversionTypes
- **[value]:** Property binding
- **{{ }}:** Interpolação

##### [(ngModel)]
```html
<select [(ngModel)]="selectedConversionType">
```
- **Two-way binding:** Sincroniza propriedade com input
- **Requer:** FormsModule importado

##### (event)
```html
<input type="file" (change)="onFileSelected($event)">
<button (click)="convertFile()">
```
- **Event binding:** Chama método quando evento ocorre

##### [disabled]
```html
<button [disabled]="!selectedFile || isLoading">
```
- **Property binding:** Desabilita botão se condição for true

**Estrutura do template:**

1. **Upload Section:** Input de arquivo + display do nome
2. **Conversion Section:** Select com tipos de conversão
3. **Action Section:** Botão de converter
4. **Error Alert:** Mensagem de erro (se houver)
5. **Success Result:** Detalhes + botão de download
6. **Info Footer:** Lista de conversões suportadas

---

#### file-converter.component.css

**Técnicas CSS modernas:**

##### Gradientes
```css
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
```
- **135deg:** Diagonal
- **Cores:** Roxo suave

##### Sombras
```css
box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
```
- **Profundidade:** Efeito de elevação

##### Transições
```css
transition: all 0.3s ease;
```
- **Animação suave:** Em hover e estados

##### Flexbox
```css
display: flex;
align-items: center;
gap: 1rem;
```
- **Layout moderno:** Espaçamento consistente

##### Grid
```css
display: grid;
grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
gap: 1rem;
```
- **Layout responsivo:** Auto-ajusta colunas

##### Animations
```css
@keyframes spin {
  to { transform: rotate(360deg); }
}

.spinner {
  animation: spin 0.8s linear infinite;
}
```
- **Loading spinner:** Rotação contínua

##### Media Queries
```css
@media (max-width: 768px) {
  .converter-card {
    padding: 1.5rem;
  }
}
```
- **Responsividade:** Ajusta para mobile

---

### 5. Configurações Angular

#### angular.json

**Seções importantes:**

```json
{
  "projects": {
    "file-converter-frontend": {
      "architect": {
        "build": {
          "options": {
            "outputPath": "dist/file-converter-frontend",
            "index": "src/index.html",
            "main": "src/main.ts",
            "styles": ["src/styles.css"],
            "scripts": []
          }
        }
      }
    }
  }
}
```

**Configurações de build:**
- **outputPath:** Onde o build será gerado
- **index:** HTML principal
- **main:** TypeScript entry point
- **styles:** CSS globais
- **scripts:** JS externos (se necessário)

**Ambientes:**
- **development:** Source maps, sem otimização
- **production:** Minificado, otimizado, hash em arquivos

---

#### tsconfig.json

**Opções importantes:**

```json
{
  "compilerOptions": {
    "target": "ES2022",              // JavaScript moderno
    "module": "ES2022",              // Módulos ES6
    "strict": true,                  // Type checking rigoroso
    "experimentalDecorators": true,  // Decorators (@Component)
    "moduleResolution": "node"       // Resolução Node.js
  }
}
```

**strict: true** habilita:
- strictNullChecks
- strictFunctionTypes
- strictBindCallApply
- strictPropertyInitialization

**Benefícios:** Menos bugs, código mais robusto

---

#### environments/environment.ts

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

**Como usar no código:**
```typescript
import { environment } from '../environments/environment';

private apiUrl = environment.apiUrl;
```

**Em produção:**
- Angular automaticamente usa `environment.prod.ts`
- Permite configurações diferentes por ambiente

---

### 6. Bootstrap e Inicialização

#### main.ts

```typescript
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

bootstrapApplication(AppComponent, appConfig)
  .catch(err => console.error(err));
```

**O que faz:**
1. Importa AppComponent (componente raiz)
2. Importa configuração
3. Bootstrap (inicializa) a aplicação
4. Captura erros de inicialização

#### app.config.ts

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter([]),
    provideHttpClient(withInterceptorsFromDi())
  ]
};
```

**Providers:**
- **provideRouter:** Habilita roteamento (se necessário)
- **provideHttpClient:** Habilita HttpClient para requisições

---

## 🚀 Guia de Deploy

### Backend - Railway

**Passos:**

1. **Criar conta:** railway.app
2. **Novo projeto:** "New Project" → "Deploy from GitHub"
3. **Selecionar repo:** Autorizar GitHub e selecionar repositório
4. **Configurar variáveis:**
   ```
   SPRING_PROFILES_ACTIVE=prod
   SERVER_PORT=8080
   ```
5. **Deploy automático:** Railway detecta pom.xml e faz build

**Comandos Maven que Railway executa:**
```bash
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

**URL gerada:** `https://seu-projeto.up.railway.app`

---

### Backend - Heroku

**Passos:**

1. **Instalar Heroku CLI:**
```bash
npm install -g heroku
```

2. **Login:**
```bash
heroku login
```

3. **Criar app:**
```bash
cd backend
heroku create file-converter-api
```

4. **Configurar variáveis:**
```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod
```

5. **Deploy:**
```bash
git push heroku main
```

**Procfile (opcional):**
```
web: java -jar target/file-converter-api-1.0.0.jar
```

---

### Frontend - Vercel

**Passos:**

1. **Instalar Vercel CLI:**
```bash
npm install -g vercel
```

2. **Build produção:**
```bash
cd frontend
npm run build
```

3. **Deploy:**
```bash
vercel
```

4. **Configurar environment:**
No dashboard Vercel, adicionar:
```
VITE_API_URL=https://sua-api.railway.app/api
```

**Importante:** Atualizar `environment.prod.ts` com URL do backend

---

### Frontend - Netlify

**Passos:**

1. **Build:**
```bash
ng build --configuration production
```

2. **Deploy manual:**
   - Acesse netlify.com
   - Drag & drop da pasta `dist/file-converter-frontend`

3. **Deploy via CLI:**
```bash
npm install -g netlify-cli
netlify deploy --prod --dir=dist/file-converter-frontend
```

**netlify.toml (opcional):**
```toml
[build]
  publish = "dist/file-converter-frontend"
  command = "npm run build"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

---

## 🐛 Troubleshooting

### Problema: CORS Error no Frontend

**Erro:**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/convert/upload' 
from origin 'http://localhost:4200' has been blocked by CORS policy
```

**Solução:**
1. Verificar `CorsConfig.java`:
```java
.allowedOrigins("http://localhost:4200")  // ← Deve incluir sua porta
```

2. Verificar se está em **desenvolvimento**:
```java
.allowedOrigins("*")  // Permite qualquer origem (só dev!)
```

3. **Em produção:**
```java
.allowedOrigins("https://seu-frontend.vercel.app")
```

---

### Problema: Arquivo muito grande

**Erro:**
```
MaxUploadSizeExceededException: Maximum upload size exceeded
```

**Solução:** Aumentar limite em `application.properties`:
```properties
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
```

---

### Problema: ClassNotFoundException ao iniciar

**Erro:**
```
java.lang.ClassNotFoundException: com.itextpdf.kernel.pdf.PdfWriter
```

**Solução:** Dependência não foi baixada corretamente

```bash
cd backend
./mvnw clean install -U  # -U força atualização
```

Se persistir, deletar pasta `.m2/repository` e refazer install

---

### Problema: Angular não encontra módulo

**Erro:**
```
Cannot find module '@angular/common/http'
```

**Solução:**
```bash
cd frontend
rm -rf node_modules
npm install
```

---

### Problema: PDF vazio ou corrompido

**Causa:** Não fechar documento ou streams

**Solução:** Sempre fechar recursos:
```java
document.close();  // ← Importante!
outputStream.flush();
```

---

### Problema: Excel com caracteres estranhos

**Causa:** Encoding incorreto

**Solução:** Usar UTF-8 consistentemente:
```java
new InputStreamReader(inputStream, StandardCharsets.UTF_8)
```

---

### Problema: Download não funciona no Angular

**Causa:** responseType não configurado

**Solução:**
```typescript
this.http.get(url, { responseType: 'blob' })  // ← Importante!
```

---

## ✅ Boas Práticas

### Backend

1. **Sempre use try-catch em conversões**
```java
try {
    converter.convert(input, output);
} catch (Exception e) {
    log.error("Conversion failed", e);
    throw new FileConversionException("Failed to convert", e);
}
```

2. **Feche streams e recursos**
```java
try (InputStream in = ...; OutputStream out = ...) {
    // código
}  // ← Fecha automaticamente
```

3. **Valide entrada**
```java
if (file.isEmpty()) {
    throw new IllegalArgumentException("File is empty");
}
```

4. **Use logs apropriadamente**
```java
log.info("Starting conversion: {}", fileName);   // Info
log.debug("Headers found: {}", headers);         // Debug
log.error("Conversion failed", exception);       // Error
```

5. **Teste edge cases**
- Arquivo vazio
- CSV malformado
- JSON inválido
- XML mal formatado

---

### Frontend

1. **Sempre faça unsubscribe** (se necessário)
```typescript
private subscription: Subscription;

ngOnInit() {
  this.subscription = this.service.getData().subscribe(...);
}

ngOnDestroy() {
  this.subscription.unsubscribe();  // Previne memory leak
}
```

**Ou use async pipe:**
```html
<div *ngIf="data$ | async as data">
  {{ data }}
</div>
```

2. **Type safety em todo lugar**
```typescript
// Bom
const response: ConversionResponse = ...;

// Ruim
const response: any = ...;  // ← Evite!
```

3. **Trate erros adequadamente**
```typescript
this.service.convert().subscribe({
  next: (data) => { /* sucesso */ },
  error: (err) => {
    console.error('Error:', err);
    this.errorMessage = 'Falha na conversão';
  }
});
```

4. **Use const/let, nunca var**
```typescript
const data = 123;    // Imutável
let count = 0;       // Mutável
// var x = 1;        // ← Nunca use!
```

5. **Organize imports**
```typescript
// Angular
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// Services
import { FileConversionService } from '../../services/...';

// Models
import { ConversionResponse } from '../../models/...';
```

---

## 📊 Diagrama de Sequência Completo

```
┌─────────┐      ┌──────────┐      ┌────────────┐      ┌──────────┐      ┌───────────┐
│ Browser │      │ Angular  │      │ Controller │      │ Service  │      │ Converter │
└────┬────┘      └────┬─────┘      └─────┬──────┘      └────┬─────┘      └─────┬─────┘
     │                │                   │                  │                  │
     │ 1. Select File │                   │                  │                  │
     │───────────────>│                   │                  │                  │
     │                │                   │                  │                  │
     │ 2. Click Convert                   │                  │                  │
     │───────────────>│                   │                  │                  │
     │                │                   │                  │                  │
     │                │ 3. POST /convert/upload             │                  │
     │                │──────────────────>│                  │                  │
     │                │                   │                  │                  │
     │                │                   │ 4. convertFile() │                  │
     │                │                   │─────────────────>│                  │
     │                │                   │                  │                  │
     │                │                   │                  │ 5. getConverter()│
     │                │                   │                  │─────────────────>│
     │                │                   │                  │                  │
     │                │                   │                  │ 6. convert()     │
     │                │                   │                  │─────────────────>│
     │                │                   │                  │                  │
     │                │                   │                  │ 7. file.xlsx     │
     │                │                   │                  │<─────────────────│
     │                │                   │                  │                  │
     │                │                   │ 8. Response      │                  │
     │                │                   │<─────────────────│                  │
     │                │                   │                  │                  │
     │                │ 9. JSON Response  │                  │                  │
     │                │<──────────────────│                  │                  │
     │                │                   │                  │                  │
     │ 10. Show Result│                   │                  │                  │
     │<───────────────│                   │                  │                  │
     │                │                   │                  │                  │
     │ 11. Click Download                 │                  │                  │
     │───────────────>│                   │                  │                  │
     │                │                   │                  │                  │
     │                │ 12. GET /download/{file}            │                  │
     │                │──────────────────>│                  │                  │
     │                │                   │                  │                  │
     │                │ 13. File (blob)   │                  │                  │
     │                │<──────────────────│                  │                  │
     │                │                   │                  │                  │
     │ 14. Download   │                   │                  │                  │
     │<───────────────│                   │                  │                  │
```

---

## 🎯 Checklist de Deploy

### Backend
- [ ] Dependências atualizadas (`./mvnw clean install`)
- [ ] Testes passando (`./mvnw test`)
- [ ] Variáveis de ambiente configuradas
- [ ] CORS configurado para domínio de produção
- [ ] Logs configurados adequadamente
- [ ] Health check endpoint funcionando
- [ ] Swagger acessível (ou desabilitado em prod)

### Frontend
- [ ] Build de produção sem erros (`ng build --prod`)
- [ ] Environment de produção configurado
- [ ] URL da API apontando para backend de produção
- [ ] Assets otimizados
- [ ] Lazy loading configurado (se aplicável)
- [ ] SEO tags configuradas (se aplicável)

---

**🎉 Parabéns! Você agora tem uma documentação completa do projeto.**

---

*Documentação criada por Diego Rapichan*  
*Última atualização: Fevereiro 2026*
