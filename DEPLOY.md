# 🚀 GUIA DE DEPLOY - File Converter

## 📦 BACKEND (Railway)

### Passo 1: Criar conta no Railway
1. Acesse: https://railway.app
2. Faça login com GitHub

### Passo 2: Deploy
1. Clique em **"New Project"**
2. Selecione **"Deploy from GitHub repo"**
3. Autorize o Railway a acessar seu GitHub
4. Selecione o repositório `java-file-converter`

### Passo 3: Configurar Backend
1. **Root Directory:** `backend`
2. **Build Command:** `./mvnw clean install -DskipTests`
3. **Start Command:** `java -jar target/file-converter-api-1.0.0.jar`
4. Clique em **Deploy**

### Passo 4: Copiar URL
Após o deploy, copie a URL gerada:
```
https://seu-projeto.up.railway.app
```

---

## 🌐 FRONTEND (Vercel)

### Passo 1: Atualizar URL da API

Edite: `frontend/src/environments/environment.prod.ts`

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://SEU-BACKEND.up.railway.app/api'  // ← Cole aqui
};
```

### Passo 2: Deploy no Vercel

**Opção A - Via CLI:**
```bash
cd frontend
npm install -g vercel
vercel --prod
```

**Opção B - Via Web:**
1. Acesse: https://vercel.com
2. Clique em **"Add New"** → **"Project"**
3. Importe do GitHub: `java-file-converter`
4. Configure:
   - **Framework Preset:** Angular
   - **Root Directory:** `frontend`
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist/file-converter-frontend`
5. Clique em **Deploy**

### Passo 3: Testar
Acesse a URL gerada:
```
https://seu-projeto.vercel.app
```

---

## ✅ CHECKLIST PÓS-DEPLOY

### Backend (Railway):
- [ ] Deploy concluído com sucesso
- [ ] URL copiada
- [ ] API respondendo em `/api/convert/health`

### Frontend (Vercel):
- [ ] `environment.prod.ts` atualizado
- [ ] Deploy concluído
- [ ] Site carregando
- [ ] Consegue conectar ao backend

### CORS:
Se der erro de CORS, edite `backend/src/main/java/com/fileconverter/config/CorsConfig.java`:

```java
.allowedOrigins(
    "http://localhost:4200",
    "https://seu-projeto.vercel.app"  // ← Adicione aqui
)
```

Depois re-deploy do backend no Railway.

---

## 🔧 TROUBLESHOOTING

### Erro: "Application failed to respond"
- Verifique se `system.properties` existe
- Confirme se Java 17 está especificado

### Erro: "Module not found"
```bash
cd frontend
rm -rf node_modules
npm install
npm run build
```

### Erro CORS:
- Atualize `CorsConfig.java` com a URL do Vercel
- Re-deploy do backend

---

## 📞 SUPORTE

Se tiver problemas:
1. Verifique os logs no Railway
2. Verifique os logs no Vercel
3. Teste a API diretamente: `https://seu-backend.up.railway.app/api/convert/health`

---

**Desenvolvido por Diego Rapichan** 🚀
