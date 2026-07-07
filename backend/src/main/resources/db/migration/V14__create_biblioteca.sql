-- =====================================================================
-- MÓDULO BIBLIOTECA
-- Observação: renomeada de V8 para V14 pois V8 já é usada por "turma".
-- =====================================================================

-- --- ACERVO: LIVROS ---
CREATE TABLE livro (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkLivro PRIMARY KEY,
    isbn            NVARCHAR(20)     NULL,
    titulo          NVARCHAR(300)    NOT NULL,
    autor           NVARCHAR(300)    NOT NULL,
    editora         NVARCHAR(200)    NULL,
    anoPublicacao   INT              NULL,
    edicao          NVARCHAR(50)     NULL,
    paginas         INT              NULL,
    categoria       NVARCHAR(100)    NULL,
    sinopse         NVARCHAR(MAX)    NULL,
    capaUrl         NVARCHAR(500)    NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfLivroCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm    DATETIME2(6)     NOT NULL CONSTRAINT dfLivroAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukLivroIsbn UNIQUE (isbn)
);

CREATE INDEX ixLivroTitulo    ON livro(titulo);
CREATE INDEX ixLivroAutor     ON livro(autor);
CREATE INDEX ixLivroCategoria ON livro(categoria);


-- --- EXEMPLARES FÍSICOS DE UM LIVRO ---
CREATE TABLE exemplar (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkExemplar PRIMARY KEY,
    livroId         UNIQUEIDENTIFIER NOT NULL,
    codigoBarras    NVARCHAR(50)     NOT NULL,
    localizacao     NVARCHAR(50)     NULL,
    status          NVARCHAR(20)     NOT NULL CONSTRAINT dfExemplarStatus DEFAULT ('DISPONIVEL'),
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfExemplarCriadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT fkExemplarLivro   FOREIGN KEY (livroId) REFERENCES livro(id),
    CONSTRAINT ukExemplarCodigo  UNIQUE (codigoBarras),
    CONSTRAINT ckExemplarStatus  CHECK (status IN ('DISPONIVEL','EMPRESTADO','RESERVADO','MANUTENCAO','PERDIDO'))
);

CREATE INDEX ixExemplarLivro  ON exemplar(livroId);
CREATE INDEX ixExemplarStatus ON exemplar(status);


-- --- EMPRÉSTIMOS DE EXEMPLARES ---
CREATE TABLE emprestimo (
    id                    UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkEmprestimo PRIMARY KEY,
    exemplarId            UNIQUEIDENTIFIER NOT NULL,
    usuarioId             UNIQUEIDENTIFIER NOT NULL,
    dataEmprestimo        DATETIME2(6)     NOT NULL,
    dataDevolucaoPrevista DATETIME2(6)     NOT NULL,
    dataDevolucaoEfetiva  DATETIME2(6)     NULL,
    renovacoes            INT              NOT NULL CONSTRAINT dfEmprestimoRenovacoes DEFAULT (0),
    status                NVARCHAR(20)     NOT NULL CONSTRAINT dfEmprestimoStatus     DEFAULT ('ATIVO'),
    criadoPorId           UNIQUEIDENTIFIER NOT NULL,
    criadoEm              DATETIME2(6)     NOT NULL CONSTRAINT dfEmprestimoCriadoEm   DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT fkEmprestimoExemplar  FOREIGN KEY (exemplarId)  REFERENCES exemplar(id),
    CONSTRAINT fkEmprestimoUsuario   FOREIGN KEY (usuarioId)   REFERENCES usuario(id),
    CONSTRAINT fkEmprestimoCriadoPor FOREIGN KEY (criadoPorId) REFERENCES usuario(id),
    CONSTRAINT ckEmprestimoStatus    CHECK (status IN ('ATIVO','DEVOLVIDO','ATRASADO')),
    CONSTRAINT ckEmprestimoDatas     CHECK (dataDevolucaoPrevista >= dataEmprestimo)
);

CREATE INDEX ixEmprestimoUsuario  ON emprestimo(usuarioId);
CREATE INDEX ixEmprestimoExemplar ON emprestimo(exemplarId);
CREATE INDEX ixEmprestimoStatus   ON emprestimo(status);


-- --- RESERVAS DE LIVROS COM FILA ---
CREATE TABLE reserva (
    id            UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkReserva PRIMARY KEY,
    livroId       UNIQUEIDENTIFIER NOT NULL,
    usuarioId     UNIQUEIDENTIFIER NOT NULL,
    dataReserva   DATETIME2(6)     NOT NULL CONSTRAINT dfReservaData   DEFAULT (SYSUTCDATETIME()),
    status        NVARCHAR(20)     NOT NULL CONSTRAINT dfReservaStatus DEFAULT ('AGUARDANDO'),
    posicaoFila   INT              NOT NULL,

    CONSTRAINT fkReservaLivro   FOREIGN KEY (livroId)   REFERENCES livro(id),
    CONSTRAINT fkReservaUsuario FOREIGN KEY (usuarioId) REFERENCES usuario(id),
    CONSTRAINT ckReservaStatus  CHECK (status IN ('AGUARDANDO','ATENDIDA','EXPIRADA','CANCELADA'))
);

CREATE INDEX ixReservaLivro    ON reserva(livroId);
CREATE INDEX ixReservaUsuario  ON reserva(usuarioId);
CREATE INDEX ixReservaStatus   ON reserva(status);


-- --- MULTAS GERADAS A PARTIR DE EMPRÉSTIMOS ATRASADOS ---
CREATE TABLE multa (
    id            UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkMulta PRIMARY KEY,
    emprestimoId  UNIQUEIDENTIFIER NOT NULL,
    valor         DECIMAL(10,2)    NOT NULL,
    diasAtraso    INT              NOT NULL,
    status        NVARCHAR(20)     NOT NULL CONSTRAINT dfMultaStatus DEFAULT ('PENDENTE'),
    geradaEm      DATETIME2(6)     NOT NULL CONSTRAINT dfMultaGerada DEFAULT (SYSUTCDATETIME()),
    pagaEm        DATETIME2(6)     NULL,

    CONSTRAINT fkMultaEmprestimo FOREIGN KEY (emprestimoId) REFERENCES emprestimo(id),
    CONSTRAINT ukMultaEmprestimo UNIQUE (emprestimoId),
    CONSTRAINT ckMultaStatus     CHECK (status IN ('PENDENTE','PAGA','CANCELADA')),
    CONSTRAINT ckMultaValor      CHECK (valor >= 0)
);

CREATE INDEX ixMultaStatus ON multa(status);


-- --- PARÂMETROS GERAIS DA BIBLIOTECA (LINHA ÚNICA) ---
CREATE TABLE configuracaoBiblioteca (
    id                          UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkConfiguracaoBiblioteca PRIMARY KEY,
    prazoEmprestimoAluno        INT              NOT NULL CONSTRAINT dfConfigPrazoAluno     DEFAULT (7),
    prazoEmprestimoProfessor    INT              NOT NULL CONSTRAINT dfConfigPrazoProfessor DEFAULT (15),
    maxEmprestimosSimultaneos   INT              NOT NULL CONSTRAINT dfConfigMaxEmprestimos DEFAULT (3),
    maxRenovacoes               INT              NOT NULL CONSTRAINT dfConfigMaxRenovacoes  DEFAULT (2),
    valorMultaDia               DECIMAL(10,2)    NOT NULL CONSTRAINT dfConfigValorMulta     DEFAULT (1.00),
    atualizadoEm                DATETIME2(6)     NOT NULL CONSTRAINT dfConfigAtualizadoEm   DEFAULT (SYSUTCDATETIME())
);

-- --- LINHA PADRÃO ---
INSERT INTO configuracaoBiblioteca (id) VALUES (NEWID());

