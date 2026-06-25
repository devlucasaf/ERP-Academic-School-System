CREATE TABLE aluno (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkAluno PRIMARY KEY,
    usuarioId       UNIQUEIDENTIFIER NOT NULL,
    matriculaRA     NVARCHAR(30)     NOT NULL,
    dataIngresso    DATE             NOT NULL,
    status          NVARCHAR(20)     NOT NULL,
    turmaAtualId    UNIQUEIDENTIFIER NULL,
    observacoes     NVARCHAR(1000)   NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfAlunoCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm    DATETIME2(6)     NOT NULL CONSTRAINT dfAlunoAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukAlunoUsuario     UNIQUE (usuarioId),
    CONSTRAINT ukAlunoMatriculaRA UNIQUE (matriculaRA),
    CONSTRAINT fkAlunoUsuario     FOREIGN KEY (usuarioId) REFERENCES usuario(id),
    CONSTRAINT ckAlunoStatus      CHECK (status IN ('ATIVO','TRANCADO','FORMADO','EVADIDO'))
);

CREATE INDEX ixAlunoStatus ON aluno(status);
CREATE INDEX ixAlunoTurma  ON aluno(turmaAtualId);

