CREATE TABLE professor (
    id                  UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkProfessor PRIMARY KEY,
    usuarioId           UNIQUEIDENTIFIER NOT NULL,
    formacao            NVARCHAR(200)    NULL,
    areaAtuacao         NVARCHAR(150)    NULL,
    cargaHorariaSemanal INT              NULL,
    dataAdmissao        DATE             NOT NULL,
    ativo               BIT              NOT NULL CONSTRAINT dfProfessorAtivo        DEFAULT (1),
    criadoEm            DATETIME2(6)     NOT NULL CONSTRAINT dfProfessorCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfProfessorAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukProfessorUsuario UNIQUE (usuarioId),
    CONSTRAINT fkProfessorUsuario FOREIGN KEY (usuarioId) REFERENCES usuario(id)
);

CREATE INDEX ixProfessorAtivo ON professor(ativo);

CREATE TABLE professorDisciplina (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkProfessorDisciplina PRIMARY KEY,
    professorId     UNIQUEIDENTIFIER NOT NULL,
    disciplinaId    UNIQUEIDENTIFIER NOT NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfProfessorDisciplinaCriadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukProfessorDisciplina          UNIQUE (professorId, disciplinaId),
    CONSTRAINT fkProfessorDisciplinaProfessor FOREIGN KEY (professorId) REFERENCES professor(id)
    -- --- FK PARA "disciplina" SERÁ ADICIONADA NA V7 (DEPOIS DE CRIAR A TABELA disciplina) ---
);

CREATE INDEX ixProfessorDisciplinaProfessor  ON professorDisciplina(professorId);
CREATE INDEX ixProfessorDisciplinaDisciplina ON professorDisciplina(disciplinaId);

