CREATE TABLE tb_responsavel (
    id                  UNIQUEIDENTIFIER    NOT NULL CONSTRAINT pk_tb_responsavel PRIMARY KEY,
    usuario_id          UNIQUEIDENTIFIER    NOT NULL,
    parentesco          NVARCHAR(20)        NOT NULL,
    criado_em           DATETIME2(6)        NOT NULL CONSTRAINT df_tb_responsavel_criado_em     DEFAULT (SYSUTCDATETIME()),
    atualizado_em       DATETIME2(6)        NOT NULL CONSTRAINT df_tb_responsavel_atualizado_em DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT uk_tb_responsavel_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_tb_responsavel_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id),
    CONSTRAINT ck_tb_responsavel_parentesco CHECK (parentesco IN ('PAI','MAE','TUTOR','OUTRO'))
);

CREATE TABLE tb_responsavel_aluno (
    id                  UNIQUEIDENTIFIER    NOT NULL CONSTRAINT pk_tb_responsavel_aluno PRIMARY KEY,
    responsavel_id      UNIQUEIDENTIFIER    NOT NULL,
    aluno_id            UNIQUEIDENTIFIER    NOT NULL,
    observacao          NVARCHAR(500)       NULL,
    criado_em           DATETIME2(6)        NOT NULL CONSTRAINT df_tb_resp_aluno_criado_em DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT uk_tb_responsavel_aluno          UNIQUE (responsavel_id, aluno_id),
    CONSTRAINT fk_tb_resp_aluno_responsavel     FOREIGN KEY (responsavel_id) REFERENCES tb_responsavel(id),
    CONSTRAINT fk_tb_resp_aluno_aluno           FOREIGN KEY (aluno_id)       REFERENCES tb_aluno(id)
);

CREATE INDEX ix_tb_resp_aluno_responsavel ON tb_responsavel_aluno(responsavel_id);
CREATE INDEX ix_tb_resp_aluno_aluno       ON tb_responsavel_aluno(aluno_id);

