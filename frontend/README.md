# 🎨 File Converter - Frontend Angular

Interface web moderna para conversão de arquivos.

## 🚀 Tecnologias

- **Angular 17** - Framework frontend
- **TypeScript** - Linguagem
- **RxJS** - Programação reativa
- **CSS3** - Estilização moderna com gradientes

## ✨ Funcionalidades

- 📁 Upload de arquivos com drag & drop
- 🔄 Seleção de tipo de conversão
- 📊 Visualização de progresso
- ⬇️ Download de arquivos convertidos
- 🎨 Design moderno e responsivo
- ⚡ Feedback em tempo real

## 📋 Pré-requisitos

- Node.js 18+ e npm
- Angular CLI 17+

## 🔧 Instalação

### 1. Instale o Angular CLI globalmente

```bash
npm install -g @angular/cli
```

### 2. Instale as dependências

```bash
cd frontend
npm install
```

### 3. Configure a URL da API

Edite `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'  // URL do backend
};
```

### 4. Execute o servidor de desenvolvimento

```bash
npm start
# ou
ng serve
```

A aplicação estará disponível em: `http://localhost:4200`

## 🏗️ Build para Produção

```bash
ng build --configuration production
```

Os arquivos de build estarão em `dist/file-converter-frontend`

## 📁 Estrutura do Projeto

```
src/
├── app/
│   ├── components/
│   │   └── file-converter/          # Componente principal
│   │       ├── file-converter.component.ts
│   │       ├── file-converter.component.html
│   │       └── file-converter.component.css
│   ├── services/
│   │   └── file-conversion.service.ts  # Service HTTP
│   ├── models/
│   │   └── conversion.model.ts      # Interfaces TypeScript
│   ├── app.component.ts             # Componente raiz
│   └── app.config.ts                # Configuração da app
├── environments/                     # Configurações de ambiente
├── assets/                           # Recursos estáticos
├── styles.css                        # Estilos globais
└── index.html                        # HTML principal
```

## 🎨 Recursos de UI

### Design Moderno

- Gradientes coloridos
- Animações suaves
- Cards com sombras
- Botões interativos
- Loading spinners
- Mensagens de alerta

### Responsividade

- Mobile-first design
- Breakpoints para tablets e desktops
- Grid system flexível

## 🔌 Integração com Backend

O frontend se comunica com o backend através do `FileConversionService`:

```typescript
// Converter arquivo
this.fileConversionService.convertFile(file, type)
  .subscribe(response => {
    // Manipular resposta
  });

// Download de arquivo
this.fileConversionService.downloadFile(fileName)
  .subscribe(blob => {
    // Fazer download
  });
```

## 🧪 Testes

```bash
ng test
```

## 📱 Screenshots

### Tela Principal
Interface de upload com seleção de tipo de conversão

### Resultado da Conversão
Detalhes do arquivo convertido com botão de download

## 🌐 Deploy

### Vercel

```bash
npm install -g vercel
vercel
```

### Netlify

```bash
ng build --configuration production
# Faça upload da pasta dist/file-converter-frontend
```

### Docker

```dockerfile
FROM node:18 as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/file-converter-frontend /usr/share/nginx/html
```

## 👨‍💻 Autor

**Diego Rapichan**
- GitHub: [@DiegoRapichan](https://github.com/DiegoRapichan)

## 📝 Licença

MIT License
