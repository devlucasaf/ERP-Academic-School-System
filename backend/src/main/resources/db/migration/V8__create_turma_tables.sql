CREATE TABLE turma (
    id                  UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkTurma PRIMARY KEY,
    codigo              NVARCHAR(50)     NOT NULL,
    cursoId             UNIQUEIDENTIFIER NOT NULL,
    periodoLetivo       NVARCHAR(20)     NOT NULL,
    serie               NVARCHAR(50)     NOT NULL,
    sala                NVARCHAR(50)     NULL,
    turno               NVARCHAR(20)     NOT NULL,
    professorRegenteId  UNIQUEIDENTIFIER NULL,
    capacidadeMaxima    INT              NOT NULL,
    ativa               BIT              NOT NULL CONSTRAINT dfTurmaAtiva        DEFAULT (1),
    criadoEm            DATETIME2(6)     NOT NULL CONSTRAINT dfTurmaCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfTurmaAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukTurmaCodigo            UNIQUE (codigo),
    CONSTRAINT fkTurmaCurso             FOREIGN KEY (cursoId)            REFERENCES curso(id),
    CONSTRAINT fkTurmaProfessorRegente  FOREIGN KEY (professorRegenteId) REFERENCES professor(id),
    CONSTRAINT ckTurmaTurno             CHECK (turno IN ('MANHA','TARDE','NOITE')),
    CONSTRAINT ckTurmaCapacidade        CHECK (capacidadeMaxima > 0)
);

CREATE INDEX ixTurmaCurso         ON turma(cursoId);
CREATE INDEX ixTurmaPeriodoLetivo ON turma(periodoLetivo);
CREATE INDEX ixTurmaAtiva         ON turma(ativa);

CREATE TABLE turmaDisciplina (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkTurmaDisciplina PRIMARY KEY,
    turmaId         UNIQUEIDENTIFIER NOT NULL,
    disciplinaId    UNIQUEIDENTIFIER NOT NULL,
    professorId     UNIQUEIDENTIFIER NOT NULL,
    diaSemana       NVARCHAR(20)     NOT NULL,
    horarioInicio   TIME             NOT NULL,
    horarioFim      TIME             NOT NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfTurmaDisciplinaCriadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukTurmaDisciplina           UNIQUE (turmaId, disciplinaId),
    CONSTRAINT fkTurmaDisciplinaTurma      FOREIGN KEY (turmaId)      REFERENCES turma(id),
    CONSTRAINT fkTurmaDisciplinaDisciplina FOREIGN KEY (disciplinaId) REFERENCES disciplina(id),
    CONSTRAINT fkTurmaDisciplinaProfessor  FOREIGN KEY (professorId)  REFERENCES professor(id),
    CONSTRAINT ckTurmaDisciplinaDiaSemana  CHECK (diaSemana IN ('SEGUNDA','TERCA','QUARTA','QUINTA','SEXTA','SABADO','DOMINGO')),
    CONSTRAINT ckTurmaDisciplinaHorario    CHECK (horarioFim > horarioInicio)
);

CREATE INDEX ixTurmaDisciplinaTurma      ON turmaDisciplina(turmaId);
CREATE INDEX ixTurmaDisciplinaDisciplina ON turmaDisciplina(disciplinaId);
CREATE INDEX ixTurmaDisciplinaProfessor  ON turmaDisciplina(professorId);

-- --- ATIVA A FK PENDENTE DA V2 (aluno.turmaAtualId -> turma.id) ---
ALTER TABLE aluno
    ADD CONSTRAINT fkAlunoTurma
        FOREIGN KEY (turmaAtualId) REFERENCES turma(id);

