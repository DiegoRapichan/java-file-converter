# 🔄 File Converter API - Backend

API REST para conversão de arquivos entre diferentes formatos.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.2**
- **Maven**
- **Apache Commons CSV** - Manipulação de CSV
- **Jackson** - JSON/XML processing
- **Apache POI** - Excel files
- **iText 7** - PDF generation
- **Swagger/OpenAPI** - Documentação interativa
- **Lombok** - Redução de boilerplate

## ✨ Funcionalidades

### Conversões Suportadas

- ✅ **CSV → JSON** - Converte CSV para array JSON
- ✅ **JSON → CSV** - Converte array JSON para CSV
- ✅ **JSON → XML** - Converte JSON para XML
- ✅ **XML → JSON** - Converte XML para JSON
- ✅ **CSV → Excel** - Converte CSV para planilha Excel (.xlsx)
- ✅ **Texto → PDF** - Converte texto simples para PDF
- ✅ **JSON → PDF** - Converte array JSON para tabela PDF

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.8+

## 🔧 Como Executar

### 1. Clone o repositório

```bash
git clone <repository-url>
cd java-file-converter/backend
```

### 2. Compile o projeto

```bash
./mvnw clean install -DskipTests
```

### 3. Execute a aplicação

```bash
./mvnw spring-boot:run
```

A API estará rodando em: `http://localhost:8080`

## 📚 Documentação da API

### Swagger UI (Interativo)

Acesse: `http://localhost:8080/swagger-ui.html`

### Endpoints Principais

#### 1. Listar tipos de conversão suportados

```http
GET /api/convert/types
```

#### 2. Converter arquivo

```http
POST /api/convert/upload
Content-Type: multipart/form-data

Parameters:
- file: arquivo para conversão
- conversionType: tipo de conversão (CSV_TO_JSON, JSON_TO_XML, etc.)
```

#### 3. Download do arquivo convertido

```http
GET /api/convert/download/{fileName}
```

#### 4. Health check

```http
GET /api/convert/health
```

## 🏗️ Arquitetura

### Padrões de Design Utilizados

1. **Strategy Pattern** - Interface `FileConverter` com implementações específicas
2. **Factory Pattern** - `ConverterFactory` para criar conversores
3. **Dependency Injection** - Spring Boot IoC Container
4. **DTO Pattern** - Separação entre entidades e respostas
5. **Exception Handling** - Global exception handler

### Estrutura de Pastas

```
src/main/java/com/fileconverter/
├── config/              # Configurações (CORS, Swagger)
├── controller/          # REST Controllers
├── service/             # Lógica de negócio
├── converter/           # Implementações dos conversores
├── factory/             # Factory para conversores
├── model/               # DTOs e enums
├── exception/           # Exception handlers
└── FileConverterApplication.java
```

## 🧪 Testes

```bash
./mvnw test
```

## 📦 Build para Produção

```bash
./mvnw clean package -DskipTests
java -jar target/file-converter-api-1.0.0.jar
```

## 🔍 Exemplos de Uso

### Exemplo com cURL

```bash
# Converter CSV para JSON
curl -X POST http://localhost:8080/api/convert/upload \
  -F "file=@data.csv" \
  -F "conversionType=CSV_TO_JSON"

# Download do arquivo
curl -O http://localhost:8080/api/convert/download/data_abc123.json
```

### Exemplo de Resposta

```json
{
  "success": true,
  "message": "File converted successfully",
  "originalFileName": "data.csv",
  "convertedFileName": "data_abc123.json",
  "conversionType": "CSV_TO_JSON",
  "fileSizeBytes": 2048,
  "downloadUrl": "/api/convert/download/data_abc123.json"
}
```

## 👨‍💻 Autor

**Diego Rapichan**
- GitHub: [@DiegoRapichan](https://github.com/DiegoRapichan)
- LinkedIn: [Diego Rapichan](https://linkedin.com/in/diego-rapichan)

## 📝 Licença

Este projeto está sob a licença MIT.
