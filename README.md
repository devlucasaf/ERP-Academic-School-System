<img
width=100%
src="https://capsule-render.vercel.app/api?type=waving&color=A020F0&height=120&section=header"
/>

<p align="center">
    <img 
        src="https://img.shields.io/badge/status-em%20progresso-yellow?style=for-the-badge" 
    />
    <img 
        src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" 
    />
    <img 
        src="https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" 
    />
    <img 
        src="https://img.shields.io/badge/SQL%20Server-CC2927?style=for-the-badge&logo=microsoftsqlserver&logoColor=white" 
    />
    <img 
        src="https://img.shields.io/badge/Vite-5-646CFF?style=for-the-badge&logo=microsoftsqlserver&logoColor=white" 
    />
    <img 
        src="https://img.shields.io/badge/license-MIT-A020F0?style=for-the-badge" 
    />
</p>


![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white)
![SQL Server](https://img.shields.io/badge/SQL%20Server-2019%2B-CC2927?logo=microsoftsqlserver&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-5-646CFF?logo=vite&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-green)

ERP de um Sistema Acadêmico Escolar — gestão de alunos, professores, turmas, disciplinas, notas, frequência, financeiro e comunicação.

---

## 🧱 Stack

### Backend
- **Java 21** + **Spring Boot 3.5**
- **Spring Web**, **Spring Data JPA**, **Spring Security**, **Spring Validation**, **Spring Mail**
- **SQL Server** (driver `mssql-jdbc`)
- **Flyway** (migrations)
- **Lombok**, **ModelMapper**
- **SpringDoc OpenAPI** (Swagger UI)
- **java-jwt (Auth0)** para autenticação JWT
- **Maven** como gerenciador de build

### Frontend
- **Vite** + **HTML / CSS / JavaScript** (vanilla, SPA simples)

### Banco de dados
- **Microsoft SQL Server** (local, porta `1433`, database `erp_academic_db`)

---

## 📁 Estrutura do projeto

```
ERP-Academic-School-System/
├── backend/                       # API Spring Boot
│   ├── pom.xml
│   └── src/main/
│       ├── java/erp/academico/
│       │   ├── ErpAcademicoApplication.java
│       │   ├── dto/auth/
│       │   ├── exception/
│       │   ├── infra/
│       │   │   ├── config/
│       │   │   ├── security/
│       │   │   ├── storage/
│       │   │   └── mail/
│       │   └── modules/
│       └── resources/
│           ├── application.yml
│           └── db/migration/
└── frontend/                      # SPA Vite
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── scripts/
        │   ├── pages/
        │   └── remotes/
        ├── styles/
        └── templates/
```

---

## ⚙️ Pré-requisitos

- **JDK 21**
- **Maven 3.9+** (ou use o wrapper `./mvnw`)
- **Node.js 18+** e **npm**
- **SQL Server** rodando em `localhost:1433` com o banco `erp_academic_db` criado

---

## 🚀 Setup

### 1. Clonar o repositório
```powershell
git clone https://github.com/<seu-usuario>/ERP-Academic-School-System.git
cd ERP-Academic-School-System
```

### 2. Banco de dados
Crie o database no SQL Server:
```sql
CREATE DATABASE erp_academic_db;
```

### 3. Backend

Defina as variáveis de ambiente (PowerShell):
```powershell
$env:DB_USER = "sa"
$env:DB_PASS = "SuaSenhaForte"
$env:JWT_SECRET = "alguma-chave-secreta-bem-grande"
```

Build e execução:
```powershell
cd backend
./mvnw spring-boot:run
```

API disponível em: **http://localhost:8080/api**
Swagger UI: **http://localhost:8080/api/swagger-ui.html**

### 4. Frontend

```powershell
cd frontend
npm install
npm run dev
```

App disponível em: **http://localhost:5173** (com proxy `/api` → `http://localhost:8080`)

---

## 📜 Licença

Distribuído sob a licença MIT. Veja `LICENSE` para mais informações.
