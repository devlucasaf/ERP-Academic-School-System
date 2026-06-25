CREATE TABLE nota (
    id                 UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkNota PRIMARY KEY,
    alunoId            UNIQUEIDENTIFIER NOT NULL,
    turmaDisciplinaId  UNIQUEIDENTIFIER NOT NULL,
    periodoAvaliacao   NVARCHAR(30)     NOT NULL,
    tipoAvaliacao      NVARCHAR(20)     NOT NULL,
    valor              DECIMAL(5,2)     NOT NULL,
    peso               DECIMAL(5,2)     NOT NULL CONSTRAINT dfNotaPeso         DEFAULT (1.00),
    observacoes        NVARCHAR(1000)   NULL,
    lancadaPor         UNIQUEIDENTIFIER NULL,
    lancadaEm          DATETIME2(6)     NOT NULL CONSTRAINT dfNotaLancadaEm    DEFAULT (SYSUTCDATETIME()),
    atualizadaEm       DATETIME2(6)     NOT NULL CONSTRAINT dfNotaAtualizadaEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT fkNotaAluno            FOREIGN KEY (alunoId)           REFERENCES aluno(id),
    CONSTRAINT fkNotaTurmaDisciplina  FOREIGN KEY (turmaDisciplinaId) REFERENCES turmaDisciplina(id),
    CONSTRAINT fkNotaLancadaPor       FOREIGN KEY (lancadaPor)        REFERENCES usuario(id),
    CONSTRAINT ckNotaPeriodoAvaliacao CHECK (periodoAvaliacao IN ('BIMESTRE_1','BIMESTRE_2','BIMESTRE_3','BIMESTRE_4','RECUPERACAO_FINAL')),
    CONSTRAINT ckNotaTipoAvaliacao    CHECK (tipoAvaliacao IN ('PROVA','TRABALHO','PARTICIPACAO','OUTRO')),
    CONSTRAINT ckNotaValor            CHECK (valor >= 0 AND valor <= 10),
    CONSTRAINT ckNotaPeso             CHECK (peso > 0)
);

CREATE INDEX ixNotaAluno            ON nota(alunoId);
CREATE INDEX ixNotaTurmaDisciplina  ON nota(turmaDisciplinaId);
CREATE INDEX ixNotaPeriodoAvaliacao ON nota(periodoAvaliacao);
