CREATE TABLE matricula (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkMatricula PRIMARY KEY,
    alunoId         UNIQUEIDENTIFIER NOT NULL,
    turmaId         UNIQUEIDENTIFIER NOT NULL,
    dataMatricula   DATE             NOT NULL,
    status          NVARCHAR(20)     NOT NULL,
    observacoes     NVARCHAR(1000)   NULL,
    criadaPor       UNIQUEIDENTIFIER NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfMatriculaCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm    DATETIME2(6)     NOT NULL CONSTRAINT dfMatriculaAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT fkMatriculaAluno     FOREIGN KEY (alunoId)   REFERENCES aluno(id),
    CONSTRAINT fkMatriculaTurma     FOREIGN KEY (turmaId)   REFERENCES turma(id),
    CONSTRAINT fkMatriculaCriadaPor FOREIGN KEY (criadaPor) REFERENCES usuario(id),
    CONSTRAINT ckMatriculaStatus    CHECK (status IN ('ATIVA','TRANCADA','CANCELADA','TRANSFERIDA','CONCLUIDA'))
);

CREATE INDEX ixMatriculaAluno  ON matricula(alunoId);
CREATE INDEX ixMatriculaTurma  ON matricula(turmaId);
CREATE INDEX ixMatriculaStatus ON matricula(status);

-- --- ÍNDICE ÚNICO PARCIAL: GARANTE NO BANCO QUE NÃO HAVERÁ DUAS MATRÍCULAS ATIVAS PARA O MESMO ALUNO NA MESMA TURMA ---
CREATE UNIQUE INDEX uxMatriculaAlunoTurmaAtiva
    ON matricula(alunoId, turmaId)
    WHERE status = 'ATIVA';
