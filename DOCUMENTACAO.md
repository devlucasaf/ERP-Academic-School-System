# 📚 Documentação Geral do Sistema (ERP Acadêmico Escolar)

> **Para quem é este documento?**
> Para você, que está construindo este projeto e quer **entender de verdade** o que existe aqui,
> sem "vibe code". Ele explica **o que é cada parte**, **por que ela existe** e **como tudo se conecta**,
> com a menor quantidade de jargão possível. Leia de cima para baixo na primeira vez.

---

## 1. O que é este sistema?

É um **ERP Acadêmico** (Sistema de Gestão Escolar). Pense num sistema que uma escola usaria para
gerenciar **alunos, professores, turmas, notas, frequência, biblioteca, etc.**

Ele é dividido em duas grandes partes que conversam entre si:

| Parte | O que é | Tecnologia | Pasta |
|-------|---------|------------|-------|
| **Backend** | O "cérebro". Guarda os dados, aplica as regras e expõe uma API. | Java + Spring Boot | `backend/` |
| **Frontend** | A "cara". As telas que o usuário vê e clica no navegador. | JavaScript puro + Vite | `frontend/` |
| **Banco de Dados** | Onde os dados ficam guardados de verdade. | SQL Server | (externo, criado via `scripts/`) |

**Fluxo mental simples:**

```
Usuário clica no navegador (Frontend)
        │  faz um pedido HTTP (ex: "me dê a lista de alunos")
        ▼
Backend recebe o pedido, valida quem é você (login/token),
aplica as regras e conversa com o Banco
        │
        ▼
Banco de Dados devolve os dados → Backend → Frontend → Tela
```

---

## 2. Arquitetura em uma frase

> **Monólito Modular:** é UMA aplicação só (um monólito), mas organizada em **módulos independentes**
> (aluno, professor, biblioteca...), cada um cuidando de um assunto. Isso deixa o código organizado
> e fácil de crescer, sem a complexidade de vários sistemas separados (microserviços).

---

## 3. O Backend (a pasta `backend/`)

O backend é escrito em **Java** com o framework **Spring Boot**. O pacote base é `erp.academico`.

### 3.1. Como cada módulo é organizado

Todo módulo (ex: `aluno`) segue **sempre a mesma estrutura de 5 pastas**. Isso é proposital: quando
você entende um módulo, entende todos.

```
modules/aluno/
├── model/        → As "tabelas" em forma de classe Java (as @Entity). É o dado em si.
├── dto/          → "Formulários" de entrada/saída. O que entra e sai pela API.
├── repository/   → Quem fala com o banco (buscar, salvar, deletar). Você quase não escreve SQL.
├── service/      → O "miolo": as regras de negócio ficam aqui.
└── rest/         → O "controlador": recebe os pedidos HTTP e responde (a porta de entrada).
```

**Analogia de um restaurante 🍽️:**
- `rest/` (Controller) = o **garçom** que recebe seu pedido.
- `service/` = a **cozinha** que prepara com as regras certas.
- `repository/` = o **estoquista** que pega/guarda ingredientes na despensa.
- `model/` = os **ingredientes** (os dados).
- `dto/` = a **comanda** (o papel do pedido/entrega), que não é o prato em si.

### 3.2. Por que separar `model` de `dto`?

- `model` (Entidade) representa **exatamente a tabela do banco**. Não queremos expor isso direto pra fora
  (segurança e acoplamento).
- `dto` (Data Transfer Object) é o que **trafega pela API**. Ex: no login você não quer devolver a senha,
  então o DTO manda só o que é seguro.

### 3.3. Os módulos que existem hoje

Dentro de `backend/src/main/java/erp/academico/modules/`:

| Módulo | Responsabilidade |
|--------|------------------|
| `autenticacao` | Login, token JWT, "quem sou eu" (`/auth/me`), refresh. |
| `usuario` | A conta de acesso (email + senha + papel/role). |
| `aluno` | Cadastro de alunos. |
| `professor` | Cadastro de professores. |
| `responsavel` | Cadastro dos responsáveis (pais/mães). |
| `funcionario` | Cadastro de funcionários. |
| `curso` | Cursos oferecidos. |
| `disciplina` | Disciplinas/matérias. |
| `turma` | Turmas. |
| `matricula` | Vínculo do aluno com turma/curso. |
| `nota` | Notas/avaliações. |
| `frequencia` | Presença/faltas. |
| `atividade` | Atividades. |
| `material` | Materiais didáticos. |
| `biblioteca` | Módulo grande: livros, exemplares, empréstimos, reservas, multas. |

### 3.4. O módulo Biblioteca (o mais completo)

Ele mostra bem como um módulo "de verdade" fica. Sub-partes:

```
modules/biblioteca/
├── livro/          → O título do livro (ex: "Dom Casmurro").
├── exemplar/       → As cópias físicas daquele livro (ex: 3 cópias do "Dom Casmurro").
├── emprestimo/     → Quando um aluno pega um exemplar emprestado.
├── reserva/        → Fila de espera quando não há exemplar disponível.
├── multa/          → Cobrança gerada por atraso na devolução.
├── configuracao/   → Regras da biblioteca (dias de empréstimo, valor da multa/dia...).
├── scheduler/      → Uma tarefa automática que roda sozinha todo dia de madrugada.
└── event/          → Eventos internos (ver "MultaGeradaEvent" abaixo).
```

**Coisas importantes que aprendemos aqui:**

- **Tarefa agendada (`scheduler/`):** existe um `@Scheduled` (agendamento) que roda **todo dia às 3h da manhã**
  (`cron = "0 0 3 * * *"`) para marcar empréstimos atrasados automaticamente. Ou seja, o sistema "trabalha sozinho".

- **Evento `MultaGeradaEvent`:** quando uma multa é gerada, disparamos um "evento interno". Hoje ninguém escuta,
  mas no futuro o **módulo financeiro** vai "ouvir" esse evento e lançar a cobrança. Isso desacopla os módulos
  (a biblioteca não precisa conhecer o financeiro).

### 3.5. A pasta `infra/` (infraestrutura)

Aqui fica o que **não é regra de negócio de um módulo**, mas dá suporte a todos:

```
infra/
├── security/   → Segurança: login, token JWT, filtro que protege as rotas.
├── config/     → Configurações gerais (ex: iniciar o frontend junto — ver seção 6).
├── email/      → Envio de "emails" (hoje só imprime no console, pra desenvolvimento).
├── seed/       → "Semente": cria o usuário admin inicial automaticamente.
└── storage/    → Guardar arquivos (upload).
```

---

## 4. Segurança e Login (como o sistema sabe quem é você)

Este é um ponto que costuma confundir, então vale a pena entender.

### 4.1. JWT — o "crachá" digital

Quando você faz login com sucesso, o backend te devolve um **token JWT**. Pense nele como um **crachá**:
- Você guarda esse crachá no navegador.
- A cada novo pedido, você mostra o crachá (envia o token no cabeçalho `Authorization`).
- O backend olha o crachá, confirma que é válido e sabe **quem você é** e **qual seu papel** (role).

O token carrega dentro dele (chamados "claims"):
- `sub` = seu email
- `uid` = seu id de usuário
- `role` = seu papel (ADMIN, PROFESSOR, ALUNO...)
- `type` = `access` (crachá normal, dura 8h) ou `refresh` (para renovar, dura 7 dias)

### 4.2. Os arquivos que fazem isso

| Arquivo | Papel |
|---------|-------|
| `TokenService` | Cria e valida o token (o "fabricante do crachá"). |
| `SecurityFilter` | Um "porteiro" que intercepta **todo** pedido, lê o crachá e libera ou barra. |
| `SecurityConfig` | Define **quais rotas são públicas** e quais exigem login. |
| `AutenticacaoController` | As rotas `/auth/login`, `/auth/refresh`, `/auth/me`, `/auth/register`. |

### 4.3. Regras de acesso definidas

- `/auth/login` e `/auth/refresh` → **públicas** (qualquer um acessa, senão ninguém loga).
- `/auth/register` → só **ADMIN** pode criar usuários novos.
- **Todo o resto** (`/api/**`) → exige estar logado (crachá válido).

### 4.4. Senha temporária

Quando um admin cadastra um aluno/professor **sem informar senha**, o sistema:
1. Gera uma **senha temporária aleatória e segura** (`GeradorSenhaTemporaria`).
2. "Envia por email" (hoje o `LogEmailService` só imprime no console).

Isso simula o mundo real: a pessoa recebe a senha e depois troca.

### 4.5. Usuário admin inicial

Na primeira vez que o sistema sobe, o `AdminSeeder` cria automaticamente:
- **Email:** `admin@escola.com`
- **Senha:** `admin123`

É "idempotente": se já existir, não cria de novo. Assim você sempre tem por onde entrar.

---

## 5. O Banco de Dados (SQL Server)

### 5.1. A grande decisão: quem cria as tabelas?

Existem duas formas comuns. Nós **escolhemos a segunda**:

| Abordagem | Como funciona | Usamos? |
|-----------|---------------|---------|
| **Flyway (migrations SQL)** | Você escreve arquivos `.sql` versionados que criam as tabelas. | ❌ Não (removemos) |
| **Hibernate `ddl-auto=update`** | O próprio Java **cria/atualiza as tabelas sozinho** a partir das classes `@Entity`. | ✅ Sim |

**Por que `ddl-auto=update`?** Porque você pediu simplicidade: "quero que o `.properties` crie as tabelas,
a arquitetura não precisa de SQL". Então, ao rodar o backend, o Hibernate olha suas classes `model/` e cria
as ~25 tabelas automaticamente.

> ⚠️ **Trade-off (o preço disso):** é ótimo para **desenvolvimento/estudo**, mas em **produção** o recomendado
> é usar migrations (Flyway) porque você tem controle e histórico das mudanças. Fica a nota para o futuro.

### 5.2. camelCase → snake_case

No Java você escreve `criadoEm`; no banco vira `criado_em`. O Hibernate faz essa conversão automática
(chama-se "naming strategy"). Por isso a tabela de config da biblioteca fica `configuracao_biblioteca`.

### 5.3. O único SQL que sobrou

`scripts/setup-database.sql`. Ele **não cria tabelas**. Ele só prepara o "terreno":
- Cria o banco `erp_academic_db`.
- Cria o login `erp_academic_user` (senha `Erp@Academic#2025!`).
- Dá permissão de dono do banco para esse login.

Você roda ele **uma vez** no SSMS (SQL Server Management Studio) antes de subir o sistema.

### 5.4. Onde fica a configuração de conexão?

No arquivo `backend/src/main/resources/application.properties`. Trechos principais:

```ini
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=erp_academic_db;...
spring.datasource.username=erp_academic_user
spring.datasource.password=Erp@Academic#2025!
spring.jpa.hibernate.ddl-auto=update      # ← Hibernate cria/atualiza as tabelas
server.port=8080
server.servlet.context-path=/api           # ← toda rota começa com /api
```

> 🐛 **Detalhe que já nos mordeu:** arquivos `.properties` no Maven são lidos em ISO-8859-1.
> Por isso **não coloque acentos nos comentários** desse arquivo, senão o build quebra
> (`MalformedInputException`).

---

## 6. O Frontend (a pasta `frontend/`)

Frontend em **JavaScript puro** (sem React/Angular), empacotado pelo **Vite**. É uma **SPA**
(Single Page Application = Aplicação de Página Única).

### 6.1. O que é uma SPA?

Existe **UMA única página HTML de verdade**: o `frontend/index.html`. Ele tem um `<div id="app"></div>` vazio.
Conforme você navega, o JavaScript **troca o conteúdo dentro desse `div`** sem recarregar a página inteira.
É por isso que parece rápido e fluido.

### 6.2. Templates x Fragmentos (aquela dúvida do DOCTYPE)

- O `index.html` é o **documento completo** (tem `<!DOCTYPE html>`, `<html>`, `<head>`, `<body>`).
- Os arquivos em `src/templates/**/*.html` são **fragmentos** (pedaços) que serão injetados no `#app`.
  Por isso eles **NÃO têm** `<!DOCTYPE>` nem `<html>` — colocar isso criaria um documento dentro de outro
  (HTML inválido).

### 6.3. A regra de ouro: HTML separado do JS

Nós reorganizamos o projeto para que:
- **HTML** fique em `frontend/src/templates/` (organizado por tópico).
- **JavaScript** fique em `frontend/src/scripts/`.
- O **JS não tenha HTML escrito dentro dele**. Quando o JS precisa criar linhas de uma tabela, ele usa
  elementos `<template>` que estão no HTML e os **clona** (`cloneNode`). Assim, cada linguagem faz seu papel.

### 6.4. O roteador (`app.js`) — o coração do frontend

O `frontend/src/scripts/app.js` é o **roteador**. Ele:
1. Olha o "hash" da URL (ex: `#/alunos`).
2. Consulta uma tabela `ROTAS` para saber qual template carregar e qual JS rodar.
3. Verifica **se a rota é pública ou exige login** e **qual papel** pode ver (guarda de rota).
4. Injeta o template no `#app` e chama a função da tela.

Trecho conceitual da tabela de rotas:

```js
ROTAS = {
  "#/home":   { modelo: "site/home.html",   publico: true,  modo: "limpo" },   // site institucional
  "#/login":  { modelo: "auth/login.html",  publico: true },
  "#/alunos": { modelo: "secretaria/alunos.html", perfis: ["ADMIN","SECRETARIA"] },
  // ...
}
```

> 📝 **Pegadinha do Vite:** os templates são carregados com `import.meta.glob(...)`. Se você criar uma
> **nova pasta** de templates, precisa **adicioná-la nesse glob** no `app.js`, senão o Vite não a inclui no build.

### 6.5. Estrutura das pastas do frontend

```
frontend/src/
├── index.html (na raiz de frontend/) → o shell único da SPA
├── scripts/
│   ├── app.js       → o roteador (explicado acima)
│   ├── auth.js      → funções de login/logout, guardar token, saber o papel
│   ├── util.js      → utilidades gerais
│   ├── remotes/     → funções que CHAMAM a API do backend (ex: remotes/alunos)
│   │   └── http.js  → o "wrapper" central de fetch (get/post/put/del)
│   └── pages/       → o JS de cada tela, organizado por perfil
│       ├── site/         → o site institucional público
│       ├── auth/         → login
│       ├── admin/, aluno/, professor/, secretaria/, biblioteca/, ...
│       └── ...
├── templates/       → todo o HTML (fragmentos), por tópico
│   ├── site/home.html
│   ├── auth/login.html
│   ├── dashboards/, secretaria/, biblioteca/, shared/ (header, sidebar, shell)
└── styles/          → CSS (global, layout, components, site...)
```

### 6.6. `remotes/http.js` — como o frontend fala com o backend

Todo pedido para a API passa por aqui. Ele centraliza:
- A URL base da API.
- Colocar o token (crachá) automaticamente no cabeçalho.
- Tratar a resposta/erros.

Expõe um objeto `http` com `get`, `post`, `put`, `del` (e versões multipart para upload). Assim, nas telas
você só chama `http.get("/alunos")` e não precisa repetir a "burocracia" toda vez.

### 6.7. O Site Institucional (`#/home`)

Foi a última grande adição: uma **landing page pública** da escola (inspirada no estilo do uniceub.br),
com navbar interativa (Sobre, Alunos, Atendimento, Notícias, Educacional), antes de o usuário logar.

- HTML: `templates/site/home.html`
- JS: `scripts/pages/site/home.js` (menu hambúrguer, rolagem suave, botão "Portal" → `#/login`)
- CSS: `styles/site.css`
- Quem não está logado é redirecionado para `#/home` (o site), não direto para o login.

> **Detalhe técnico:** os links do menu usam `data-scroll` + `scrollIntoView` (rolagem suave) em vez de
> âncoras `#secao`. Isso evita conflito com o roteador, que usa `#` para trocar de página.

---

## 7. Como o backend inicia o frontend sozinho (`FrontendStarter`)

Você pediu que, ao rodar o backend no IntelliJ, o **frontend suba junto** automaticamente.

O arquivo `infra/config/FrontendStarter.java` faz isso:
- Quando o Spring termina de subir (`ApplicationReadyEvent`), ele executa `npm run dev` na pasta `frontend/`.
- Roda numa thread separada e tem um "gancho de desligamento" (shutdown hook) para encerrar o Vite
  quando você para o backend.
- Abre o navegador automaticamente.
- É controlado pela propriedade `app.dev.start-frontend=true` (pode desligar mudando para `false`).

Por causa disso, **removemos** as configurações de run separadas do frontend (senão o Vite subiria duas vezes).

---

## 8. Como rodar o projeto (passo a passo)

1. **Pré-requisitos:** Java 17, SQL Server rodando, Node.js instalado, IntelliJ.
2. **Banco:** abra o **SSMS** e rode o script `scripts/setup-database.sql` (cria o banco + login).
   - Garanta que o SQL Server está em **modo de autenticação misto** (SQL + Windows).
3. **Frontend (dependências):** dentro de `frontend/`, rode `npm install` (uma vez).
4. **Backend:** rode a classe `ErpAcademicoApplication` no IntelliJ.
   - O Hibernate cria as tabelas sozinho.
   - O `AdminSeeder` cria o `admin@escola.com` / `admin123`.
   - O `FrontendStarter` sobe o Vite e abre o navegador.
5. **Entre** com o admin e comece a usar.

**Endereços úteis:**
- Frontend (Vite): `http://localhost:5173` (padrão do Vite)
- Backend API: `http://localhost:8080/api`
- Swagger (documentação da API): `http://localhost:8080/api/swagger-ui.html`

---

## 9. Convenções que seguimos (o "estilo da casa")

Para o código ficar consistente, combinamos algumas regras:

- **Pacote base:** `erp.academico` (nunca `com.erpacademic`).
- **Identificadores em português:** classes, métodos, variáveis, atributos e funções em português
  (nomes de arquivos podem ficar como estão). Ex: `verificar()`, `obterToken()`, `criarEntidade()`.
  - **Exceção:** contratos técnicos que **não podem** mudar ficam no original — valores de claim do JWT
    (`role`, `uid`, `type`), verbos HTTP, chaves de configuração, etc.
- **Sem `record`:** usamos classes tradicionais com **Lombok** (anotações que geram getters/setters etc.).
- **Comentários em maiúsculas** no padrão: `// --- COMENTARIO ---`.
- **Commits no padrão Conventional Commits** (`feat:`, `refactor:`, `docs:`, `build:`...).
- **Criação automática de `Usuario`:** ao cadastrar uma pessoa (aluno, professor...), o sistema já cria
  a conta de acesso com o papel correto.

---

## 10. Glossário rápido (decodificando os termos)

| Termo | Tradução mental |
|-------|-----------------|
| **Backend** | O servidor/cérebro (Java). |
| **Frontend** | As telas (JavaScript). |
| **API** | O "cardápio" de pedidos que o backend aceita (URLs). |
| **Endpoint / Rota** | Um item desse cardápio (ex: `GET /api/alunos`). |
| **Entity (`@Entity`)** | Uma classe Java que vira uma tabela no banco. |
| **DTO** | Objeto de transporte de dados (o que entra/sai na API). |
| **Repository** | Quem busca/salva no banco por você. |
| **Service** | Onde ficam as regras de negócio. |
| **Controller (`rest/`)** | Recebe os pedidos HTTP e responde. |
| **JWT / Token** | O "crachá" que prova quem você é. |
| **Role / Perfil** | Seu papel no sistema (ADMIN, PROFESSOR, ALUNO...). |
| **SPA** | App de página única; troca telas sem recarregar. |
| **Hibernate / JPA** | Ferramenta que traduz Java ↔ Banco (e cria tabelas). |
| **ddl-auto** | Config que manda o Hibernate criar/atualizar tabelas. |
| **Flyway** | Ferramenta de migrations SQL (que decidimos NÃO usar). |
| **Vite** | Ferramenta que roda/empacota o frontend. |
| **Scheduler (`@Scheduled`)** | Tarefa que roda sozinha num horário. |
| **Seed / Seeder** | Dados iniciais criados automaticamente (o admin). |
| **Idempotente** | Rodar de novo não duplica nada. |

---

## 11. Mapa mental final (resumo de tudo)

```
┌──────────────────────────────────────────────────────────────┐
│                      NAVEGADOR (usuário)                       │
│  Site público (#/home)  →  Login (#/login)  →  Dashboards      │
│  Frontend: JS puro + Vite (SPA) — index.html + templates + JS  │
└───────────────────────────┬──────────────────────────────────┘
                            │ HTTP + token JWT (crachá)
                            ▼
┌──────────────────────────────────────────────────────────────┐
│                    BACKEND (Spring Boot, /api)                 │
│  Porteiro (SecurityFilter) → valida o crachá                   │
│  Módulos: aluno, professor, biblioteca, nota, ...              │
│    cada um: rest → service → repository → model                │
│  Infra: security, email, seed(admin), config(FrontendStarter)  │
└───────────────────────────┬──────────────────────────────────┘
                            │ Hibernate (JPA) cria/usa as tabelas
                            ▼
┌──────────────────────────────────────────────────────────────┐
│                 SQL SERVER (erp_academic_db)                   │
│   ~25 tabelas criadas automaticamente (ddl-auto=update)        │
└──────────────────────────────────────────────────────────────┘
```

---

> **Próximos passos sugeridos (quando quiser evoluir):**
> - Padronizar templates antigos da biblioteca (ainda têm `</body></html>` do formato antigo).
> - Implementar o **módulo financeiro** que vai "ouvir" o `MultaGeradaEvent`.
> - Trocar o `LogEmailService` por um envio de email real.
> - Em produção: considerar voltar para migrations (Flyway) no lugar de `ddl-auto=update`.

*Documento gerado para servir de guia de estudo do projeto. Atualize-o conforme o sistema evoluir.* 🚀

