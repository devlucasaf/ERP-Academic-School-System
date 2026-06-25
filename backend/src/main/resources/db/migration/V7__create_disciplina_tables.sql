CREATE TABLE disciplina (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkDisciplina PRIMARY KEY,
    codigo          NVARCHAR(20)     NOT NULL,
    nome            NVARCHAR(150)    NOT NULL,
    ementa          NVARCHAR(4000)   NULL,
    cargaHoraria    INT              NOT NULL,
    cursoId         UNIQUEIDENTIFIER NOT NULL,
    periodo         INT              NOT NULL,
    ativo           BIT              NOT NULL CONSTRAINT dfDisciplinaAtivo        DEFAULT (1),
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfDisciplinaCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm    DATETIME2(6)     NOT NULL CONSTRAINT dfDisciplinaAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukDisciplinaCodigo  UNIQUE (codigo),
    CONSTRAINT fkDisciplinaCurso   FOREIGN KEY (cursoId) REFERENCES curso(id),
    CONSTRAINT ckDisciplinaCarga   CHECK (cargaHoraria > 0),
    CONSTRAINT ckDisciplinaPeriodo CHECK (periodo > 0)
);

CREATE INDEX ixDisciplinaCurso   ON disciplina(cursoId);
CREATE INDEX ixDisciplinaPeriodo ON disciplina(periodo);
CREATE INDEX ixDisciplinaAtivo   ON disciplina(ativo);

CREATE TABLE disciplinaPrerequisito (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkDisciplinaPrerequisito PRIMARY KEY,
    disciplinaId    UNIQUEIDENTIFIER NOT NULL,
    prerequisitoId  UNIQUEIDENTIFIER NOT NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfDisciplinaPrerequisitoCriadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukDisciplinaPrerequisito              UNIQUE (disciplinaId, prerequisitoId),
    CONSTRAINT fkDisciplinaPrerequisitoDisciplina    FOREIGN KEY (disciplinaId)   REFERENCES disciplina(id),
    CONSTRAINT fkDisciplinaPrerequisitoPrerequisito  FOREIGN KEY (prerequisitoId) REFERENCES disciplina(id),
    CONSTRAINT ckDisciplinaPrerequisitoNaoSelf       CHECK (disciplinaId <> prerequisitoId)
);

CREATE INDEX ixDisciplinaPrerequisitoDisciplina   ON disciplinaPrerequisito(disciplinaId);
CREATE INDEX ixDisciplinaPrerequisitoPrerequisito ON disciplinaPrerequisito(prerequisitoId);

-- --- ATIVA A FK PENDENTE DA V4 (professorDisciplina.disciplinaId -> disciplina.id) ---
ALTER TABLE professorDisciplina
    ADD CONSTRAINT fkProfessorDisciplinaDisciplina
        FOREIGN KEY (disciplinaId) REFERENCES disciplina(id);

