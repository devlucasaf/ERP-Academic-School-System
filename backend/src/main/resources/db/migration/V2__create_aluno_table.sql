CREATE TABLE tb_aluno (
    id                  UNIQUEIDENTIFIER    NOT NULL CONSTRAINT pk_tb_aluno PRIMARY KEY,
    usuario_id          UNIQUEIDENTIFIER    NOT NULL,
    matricula_ra        NVARCHAR(30)        NOT NULL,
    data_ingresso       DATE                NOT NULL,
    status              NVARCHAR(20)        NOT NULL,
    turma_atual_id      UNIQUEIDENTIFIER    NULL,
    observacoes         NVARCHAR(1000)      NULL,
    criado_em           DATETIME2(6)        NOT NULL CONSTRAINT df_tb_aluno_criado_em     DEFAULT (SYSUTCDATETIME()),
    atualizado_em       DATETIME2(6)        NOT NULL CONSTRAINT df_tb_aluno_atualizado_em DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT uk_tb_aluno_usuario      UNIQUE (usuario_id),
    CONSTRAINT uk_tb_aluno_matricula_ra UNIQUE (matricula_ra),
    CONSTRAINT fk_tb_aluno_usuario      FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id),
    CONSTRAINT ck_tb_aluno_status       CHECK (status IN ('ATIVO','TRANCADO','FORMADO','EVADIDO'))
);

CREATE INDEX ix_tb_aluno_status     ON tb_aluno(status);
CREATE INDEX ix_tb_aluno_turma      ON tb_aluno(turma_atual_id);

