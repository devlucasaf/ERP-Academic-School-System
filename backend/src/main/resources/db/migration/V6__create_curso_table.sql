CREATE TABLE curso (
    id                  UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkCurso PRIMARY KEY,
    nome                NVARCHAR(150)    NOT NULL,
    descricao           NVARCHAR(1000)   NULL,
    nivel               NVARCHAR(20)     NOT NULL,
    duracaoSemestres    INT              NOT NULL,
    cargaHorariaTotal   INT              NOT NULL,
    ativo               BIT              NOT NULL CONSTRAINT dfCursoAtivo        DEFAULT (1),
    criadoEm            DATETIME2(6)     NOT NULL CONSTRAINT dfCursoCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfCursoAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ckCursoNivel            CHECK (nivel IN ('FUNDAMENTAL','MEDIO','TECNICO','SUPERIOR')),
    CONSTRAINT ckCursoDuracaoPositiva  CHECK (duracaoSemestres > 0),
    CONSTRAINT ckCursoCargaPositiva    CHECK (cargaHorariaTotal > 0)
);

CREATE INDEX ixCursoNivel ON curso(nivel);
CREATE INDEX ixCursoAtivo ON curso(ativo);

