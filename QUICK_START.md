# 🚀 GUIA RÁPIDO DE INSTALAÇÃO

## ⚡ Quick Start (5 minutos)

### Pré-requisitos
- ✅ Java 17+ instalado ([Download](https://adoptium.net/))
- ✅ Node.js 18+ e npm ([Download](https://nodejs.org/))
- ✅ Angular CLI: `npm install -g @angular/cli`

---

## 📦 Instalação

### 1. Extrair o projeto
```bash
tar -xzf java-file-converter.tar.gz
cd java-file-converter
```

### 2. Backend (Terminal 1)
```bash
cd backend
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

✅ Backend rodando em: **http://localhost:8080**  
📚 Swagger UI: **http://localhost:8080/swagger-ui.html**

### 3. Frontend (Terminal 2)
```bash
cd ../frontend
npm install
npm start
```

✅ Frontend rodando em: **http://localhost:4200**

---

## ✨ Testar a aplicação

1. Abra **http://localhost:4200**
2. Clique em "Escolher arquivo"
3. Selecione um arquivo CSV, JSON, XML ou TXT
4. Escolha o tipo de conversão
5. Clique em "Converter Arquivo"
6. Baixe o arquivo convertido

---

## 🧪 Testar via Swagger

1. Abra **http://localhost:8080/swagger-ui.html**
2. Expanda `POST /api/convert/upload`
3. Clique em "Try it out"
4. Faça upload de um arquivo
5. Selecione o tipo de conversão
6. Execute e veja o resultado

---

## 📁 Estrutura do Projeto

```
java-file-converter/
├── backend/              # Spring Boot API
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── frontend/             # Angular App
│   ├── src/
│   ├── package.json
│   └── README.md
├── README.md             # Documentação principal
└── DOCUMENTATION.md      # Documentação técnica completa
```

---

## 🐛 Problemas Comuns

### Backend não inicia
```bash
# Verificar versão do Java
java -version  # Deve ser 17 ou superior

# Limpar e reinstalar
cd backend
./mvnw clean install -U
```

### Frontend não inicia
```bash
# Reinstalar dependências
cd frontend
rm -rf node_modules
npm install

# Verificar versão do Node
node -v  # Deve ser 18 ou superior
```

### CORS Error
Verifique se o backend está rodando na porta 8080 e o frontend na 4200.

---

## 📚 Documentação Completa

Leia **DOCUMENTATION.md** para:
- Explicação detalhada de cada componente
- Arquitetura e design patterns
- Guia de deploy
- Troubleshooting avançado
- Boas práticas

---

## 🎯 Próximos Passos

1. **Explore o código:**
   - Backend: `backend/src/main/java/com/fileconverter/`
   - Frontend: `frontend/src/app/`

2. **Teste todas as conversões:**
   - CSV ↔ JSON
   - JSON ↔ XML
   - CSV → Excel
   - Texto → PDF
   - JSON → PDF

3. **Customize:**
   - Adicione novos tipos de conversão
   - Personalize a UI
   - Implemente autenticação

4. **Deploy:**
   - Backend: Railway, Heroku
   - Frontend: Vercel, Netlify

---

## 💡 Dicas

- Use **Swagger UI** para testar a API rapidamente
- Arquivos de exemplo estão em `backend/input/`
- Arquivos convertidos ficam em `backend/output/`
- Logs detalhados em `INFO` e `DEBUG` levels

---

## 🆘 Suporte

Se encontrar problemas:
1. Leia **DOCUMENTATION.md** seção Troubleshooting
2. Verifique logs do console
3. Abra uma issue no GitHub

---

**Desenvolvido por Diego Rapichan**  
📧 direrapichan@gmail.com  
🔗 [github.com/DiegoRapichan](https://github.com/DiegoRapichan)

---

**Bom desenvolvimento! 🚀**
