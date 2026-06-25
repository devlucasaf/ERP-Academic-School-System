CREATE TABLE usuario (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkUsuario PRIMARY KEY,
    nome            NVARCHAR(150)    NOT NULL,
    email           NVARCHAR(150)    NOT NULL,
    senha           NVARCHAR(255)    NOT NULL,
    cpf             NVARCHAR(14)     NULL,
    telefone        NVARCHAR(20)     NULL,
    dataNascimento  DATE             NULL,
    ativo           BIT              NOT NULL CONSTRAINT dfUsuarioAtivo        DEFAULT (1),
    role            NVARCHAR(30)     NOT NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfUsuarioCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm    DATETIME2(6)     NOT NULL CONSTRAINT dfUsuarioAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukUsuarioEmail UNIQUE (email),
    CONSTRAINT ckUsuarioRole  CHECK (role IN (
        'ALUNO',
        'PROFESSOR',
        'COORDENADOR',
        'SECRETARIA',
        'BIBLIOTECARIO',
        'FINANCEIRO',
        'RESPONSAVEL',
        'ADMIN'
    ))
);

CREATE INDEX ixUsuarioRole  ON usuario(role);
CREATE INDEX ixUsuarioAtivo ON usuario(ativo);

