CREATE TABLE tb_professor (
    id                       UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_tb_professor PRIMARY KEY,
    usuario_id               UNIQUEIDENTIFIER NOT NULL,
    formacao                 NVARCHAR(200)    NULL,
    area_atuacao             NVARCHAR(150)    NULL,
    carga_horaria_semanal    INT              NULL,
    data_admissao            DATE             NOT NULL,
    ativo                    BIT              NOT NULL CONSTRAINT df_tb_professor_ativo         DEFAULT (1),
    criado_em                DATETIME2(6)     NOT NULL CONSTRAINT df_tb_professor_criado_em     DEFAULT (SYSUTCDATETIME()),
    atualizado_em            DATETIME2(6)     NOT NULL CONSTRAINT df_tb_professor_atualizado_em DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT uk_tb_professor_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_tb_professor_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id)
);

CREATE INDEX ix_tb_professor_ativo ON tb_professor(ativo);

CREATE TABLE tb_professor_disciplina (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_tb_prof_disc PRIMARY KEY,
    professor_id    UNIQUEIDENTIFIER NOT NULL,
    disciplina_id   UNIQUEIDENTIFIER NOT NULL,
    criado_em       DATETIME2(6)     NOT NULL CONSTRAINT df_tb_prof_disc_criado_em DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT uk_tb_professor_disciplina     UNIQUE (professor_id, disciplina_id),
    CONSTRAINT fk_tb_prof_disc_professor      FOREIGN KEY (professor_id) REFERENCES tb_professor(id)
    -- FK para tb_disciplina será adicionada quando o módulo "disciplina" existir
);

CREATE INDEX ix_tb_prof_disc_professor  ON tb_professor_disciplina(professor_id);
CREATE INDEX ix_tb_prof_disc_disciplina ON tb_professor_disciplina(disciplina_id);

