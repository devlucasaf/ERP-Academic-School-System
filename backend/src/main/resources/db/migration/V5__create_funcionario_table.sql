CREATE TABLE tb_funcionario (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_tb_funcionario PRIMARY KEY,
    usuario_id      UNIQUEIDENTIFIER NOT NULL,
    cargo           NVARCHAR(30)     NOT NULL,
    data_admissao   DATE             NOT NULL,
    departamento    NVARCHAR(100)    NULL,
    criado_em       DATETIME2(6)     NOT NULL CONSTRAINT df_tb_funcionario_criado_em     DEFAULT (SYSUTCDATETIME()),
    atualizado_em   DATETIME2(6)     NOT NULL CONSTRAINT df_tb_funcionario_atualizado_em DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT uk_tb_funcionario_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_tb_funcionario_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id),
    CONSTRAINT ck_tb_funcionario_cargo   CHECK (cargo IN (
        'COORDENADOR',
        'SECRETARIA',
        'BIBLIOTECARIO',
        'FINANCEIRO',
        'ADMIN'
    ))
);

CREATE INDEX ix_tb_funcionario_cargo        ON tb_funcionario(cargo);
CREATE INDEX ix_tb_funcionario_departamento ON tb_funcionario(departamento);

