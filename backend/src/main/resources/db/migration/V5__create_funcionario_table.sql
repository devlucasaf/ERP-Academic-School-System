CREATE TABLE funcionario (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkFuncionario PRIMARY KEY,
    usuarioId       UNIQUEIDENTIFIER NOT NULL,
    cargo           NVARCHAR(30)     NOT NULL,
    dataAdmissao    DATE             NOT NULL,
    departamento    NVARCHAR(100)    NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfFuncionarioCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm    DATETIME2(6)     NOT NULL CONSTRAINT dfFuncionarioAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukFuncionarioUsuario UNIQUE (usuarioId),
    CONSTRAINT fkFuncionarioUsuario FOREIGN KEY (usuarioId) REFERENCES usuario(id),
    CONSTRAINT ckFuncionarioCargo   CHECK (cargo IN (
        'COORDENADOR',
        'SECRETARIA',
        'BIBLIOTECARIO',
        'FINANCEIRO',
        'ADMIN'
    ))
);

CREATE INDEX ixFuncionarioCargo        ON funcionario(cargo);
CREATE INDEX ixFuncionarioDepartamento ON funcionario(departamento);

