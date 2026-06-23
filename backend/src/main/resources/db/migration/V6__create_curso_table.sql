CREATE TABLE tb_curso (
    id                      UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_tb_curso PRIMARY KEY,
    nome                    NVARCHAR(150)    NOT NULL,
    descricao               NVARCHAR(1000)   NULL,
    nivel                   NVARCHAR(20)     NOT NULL,
    duracao_semestres       INT              NOT NULL,
    carga_horaria_total     INT              NOT NULL,
    ativo                   BIT              NOT NULL CONSTRAINT df_tb_curso_ativo         DEFAULT (1),
    criado_em               DATETIME2(6)     NOT NULL CONSTRAINT df_tb_curso_criado_em     DEFAULT (SYSUTCDATETIME()),
    atualizado_em           DATETIME2(6)     NOT NULL CONSTRAINT df_tb_curso_atualizado_em DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ck_tb_curso_nivel              CHECK (nivel IN ('FUNDAMENTAL','MEDIO','TECNICO','SUPERIOR')),
    CONSTRAINT ck_tb_curso_duracao_positiva   CHECK (duracao_semestres > 0),
    CONSTRAINT ck_tb_curso_carga_positiva     CHECK (carga_horaria_total > 0)
);

CREATE INDEX ix_tb_curso_nivel ON tb_curso(nivel);
CREATE INDEX ix_tb_curso_ativo ON tb_curso(ativo);

