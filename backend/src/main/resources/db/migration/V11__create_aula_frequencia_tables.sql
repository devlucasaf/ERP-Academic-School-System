CREATE TABLE aula (
    id                  UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkAula PRIMARY KEY,
    turmaDisciplinaId   UNIQUEIDENTIFIER NOT NULL,
    dataAula            DATE             NOT NULL,
    conteudoMinistrado  NVARCHAR(4000)   NULL,
    professorId         UNIQUEIDENTIFIER NOT NULL,
    criadaEm            DATETIME2(6)     NOT NULL CONSTRAINT dfAulaCriadaEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT fkAulaTurmaDisciplina FOREIGN KEY (turmaDisciplinaId) REFERENCES turmaDisciplina(id),
    CONSTRAINT fkAulaProfessor       FOREIGN KEY (professorId)       REFERENCES professor(id)
);

CREATE INDEX ixAulaTurmaDisciplina ON aula(turmaDisciplinaId);
CREATE INDEX ixAulaData            ON aula(dataAula);
CREATE INDEX ixAulaProfessor       ON aula(professorId);

CREATE TABLE frequencia (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkFrequencia PRIMARY KEY,
    aulaId          UNIQUEIDENTIFIER NOT NULL,
    alunoId         UNIQUEIDENTIFIER NOT NULL,
    presente        BIT              NOT NULL CONSTRAINT dfFrequenciaPresente     DEFAULT (1),
    justificativa   NVARCHAR(1000)   NULL,
    criadaEm        DATETIME2(6)     NOT NULL CONSTRAINT dfFrequenciaCriadaEm     DEFAULT (SYSUTCDATETIME()),
    atualizadaEm    DATETIME2(6)     NOT NULL CONSTRAINT dfFrequenciaAtualizadaEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukFrequenciaAulaAluno UNIQUE (aulaId, alunoId),
    CONSTRAINT fkFrequenciaAula      FOREIGN KEY (aulaId)  REFERENCES aula(id),
    CONSTRAINT fkFrequenciaAluno     FOREIGN KEY (alunoId) REFERENCES aluno(id)
);

CREATE INDEX ixFrequenciaAula  ON frequencia(aulaId);
CREATE INDEX ixFrequenciaAluno ON frequencia(alunoId);

