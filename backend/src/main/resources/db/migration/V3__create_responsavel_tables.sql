CREATE TABLE responsavel (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkResponsavel PRIMARY KEY,
    usuarioId       UNIQUEIDENTIFIER NOT NULL,
    parentesco      NVARCHAR(20)     NOT NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfResponsavelCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm    DATETIME2(6)     NOT NULL CONSTRAINT dfResponsavelAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukResponsavelUsuario    UNIQUE (usuarioId),
    CONSTRAINT fkResponsavelUsuario    FOREIGN KEY (usuarioId) REFERENCES usuario(id),
    CONSTRAINT ckResponsavelParentesco CHECK (parentesco IN ('PAI','MAE','TUTOR','OUTRO'))
);

CREATE TABLE responsavelAluno (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkResponsavelAluno PRIMARY KEY,
    responsavelId   UNIQUEIDENTIFIER NOT NULL,
    alunoId         UNIQUEIDENTIFIER NOT NULL,
    observacao      NVARCHAR(500)    NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfResponsavelAlunoCriadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukResponsavelAluno              UNIQUE (responsavelId, alunoId),
    CONSTRAINT fkResponsavelAlunoResponsavel   FOREIGN KEY (responsavelId) REFERENCES responsavel(id),
    CONSTRAINT fkResponsavelAlunoAluno         FOREIGN KEY (alunoId)       REFERENCES aluno(id)
);

CREATE INDEX ixResponsavelAlunoResponsavel ON responsavelAluno(responsavelId);
CREATE INDEX ixResponsavelAlunoAluno       ON responsavelAluno(alunoId);

