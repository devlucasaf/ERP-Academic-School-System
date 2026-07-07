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


ERP de um Sistema Acadêmico Escolar — gestão de alunos, professores, turmas, disciplinas, notas, frequência, financeiro e comunicação.

---

## 🧱 Stack

### Backend
- **Java 17** + **Spring Boot 3.5**
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

## 🛠️ Tecnologias

<div align="center">
    <img 
        alt="Java" 
        title="Java" 
        width="40px" 
        style="padding: 5px;" 
        src="https://skillicons.dev/icons?i=java" 
    />
    <img 
        alt="Spring" 
        title="Spring" 
        width="40px" 
        style="padding: 5px;" 
        src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/spring/spring-original.svg" 
    />
    <img 
        alt="JavaScript" 
        title="JavaScript" 
        width="40px" 
        style="padding: 5px;" 
        src="https://skillicons.dev/icons?i=javascript" 
    />
    <img 
        alt="Vite" 
        title="Vite" 
        width="40px" 
        style="padding: 5px;" 
        src="https://skillicons.dev/icons?i=vite" 
    />
    <img 
        alt="HTML" 
        title="HTML" 
        width="40px" 
        style="padding: 5px;" 
        src="https://skillicons.dev/icons?i=html" 
    />
    <img 
        alt="CSS" 
        title="CSS" 
        width="40px" 
        style="padding: 5px;" 
        src="https://skillicons.dev/icons?i=css" 
    />
    <img 
        alt="SqlServer" 
        title="SqlServer" 
        width="40px" 
        style="padding: 5px;" 
        src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/microsoftsqlserver/microsoftsqlserver-original.svg" 
    />
    <img 
        alt="Git" 
        title="Git" 
        width="40px" 
        style="padding: 5px;" 
        src="https://skillicons.dev/icons?i=git" 
    />
    <img 
        alt="GitHub" 
        title="GitHub" 
        width="40px" 
        style="padding: 5px;" 
        src="https://skillicons.dev/icons?i=github" 
    />
    <img 
        alt="IntelliJ" 
        title="IntelliJ IDEA" 
        width="40px" 
        style="padding: 5px;" 
        src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/intellij/intellij-original.svg" 
    />
    <img 
        alt="VS Code" 
        title="VS Code" 
        width="40px" 
        style="padding: 5px;" 
        src="https://skillicons.dev/icons?i=vscode" 
    />
</div>

---

## ⚙️ Pré-requisitos

- **JDK 17**
- **Maven 3.9+** (ou use o wrapper `./mvnw`)
- **Node.js 18+** e **npm**
- **SQL Server 2019+** rodando em `localhost:1433` (com autenticação SQL/mixed mode habilitada)
- **SSMS** (SQL Server Management Studio) para executar o script inicial

---

## 🚀 Setup

### 1. Clonar o repositório
```powershell
git clone https://github.com/<seu-usuario>/ERP-Academic-School-System.git
cd ERP-Academic-School-System
```

### 2. Banco de dados (SQL Server + SSMS)

O projeto usa **Flyway** para versionar o schema. Você só precisa criar o **banco, o login e as permissões** uma única vez — as tabelas são criadas automaticamente pela aplicação.

**Passo a passo no SSMS:**

1. Abra o **SQL Server Management Studio** e conecte-se à sua instância como administrador (ex.: login `sa`).
2. Abra o arquivo [`scripts/setup-database.sql`](scripts/setup-database.sql) (menu **File > Open > File...**).
3. Execute o script (**F5**). Ele cria:
   - o banco **`erp_academic_db`** (se não existir);
   - o login SQL **`erp_academic_user`** com senha `Erp@Academic#2025!`;
   - o usuário de banco correspondente com permissão **`db_owner`**.
4. Garanta que a instância esteja em **"SQL Server and Windows Authentication mode"**:
   - clique com o botão direito na instância > **Properties > Security**;
   - selecione **"SQL Server and Windows Authentication mode"** e **reinicie o serviço** do SQL Server.

> 💡 Em produção, troque a senha do script e defina as variáveis `DB_USER` / `DB_PASS`
> (veja a seção do backend) em vez de usar os valores padrão.

### 3. Como o Flyway aplica as migrations

Ao subir o Spring Boot, o **Flyway roda automaticamente** e aplica, em ordem, todos os
scripts em `backend/src/main/resources/db/migration/` que ainda não foram executados:

- Config em `application.yml` (`spring.flyway`): `locations: classpath:db/migration`,
  `baseline-on-migrate: true`, `validate-on-migrate: true`.
- Cada migration `V1 … V15` é aplicada **uma única vez**; o Flyway registra o histórico
  na tabela `flyway_schema_history` (criada por ele) e valida o checksum a cada boot.
- **Não edite** uma migration já aplicada — isso causa erro de checksum. Para mudanças,
  crie uma nova migration `V16__...sql`.
- A migration **`V15__seed_admin.sql`** cria o usuário administrador inicial:
  - **email:** `admin@escola.com`
  - **senha:** `admin123` (armazenada como hash BCrypt)

  Troque essa senha após o primeiro login.

### 4. Backend

Defina as variáveis de ambiente (PowerShell) — opcional em dev, pois há defaults no `application.yml`:
```powershell
$env:DB_USER = "erp_academic_user"
$env:DB_PASS = "Erp@Academic#2025!"
$env:JWT_SECRET = "alguma-chave-secreta-bem-grande"
```

Build e execução:
```powershell
cd backend
./mvnw spring-boot:run
```

API disponível em: **http://localhost:8080/api**
Swagger UI: **http://localhost:8080/api/swagger-ui.html**

### 5. Frontend

```powershell
cd frontend
npm install
npm run dev
```

App disponível em: **http://localhost:5173** (com proxy `/api` → `http://localhost:8080`)

---

## 📜 Licença

Distribuído sob a licença MIT. Veja `LICENSE` para mais informações.
