CREATE TABLE tb_usuario (
    id                  UNIQUEIDENTIFIER    NOT NULL CONSTRAINT pk_tb_usuario PRIMARY KEY,
    nome                NVARCHAR(150)       NOT NULL,
    email               NVARCHAR(150)       NOT NULL,
    senha               NVARCHAR(255)       NOT NULL,
    cpf                 NVARCHAR(14)        NULL,
    telefone            NVARCHAR(20)        NULL,
    data_nascimento     DATE                NULL,
    ativo               BIT                 NOT NULL CONSTRAINT df_tb_usuario_ativo DEFAULT (1),
    role                NVARCHAR(30)        NOT NULL,
    criado_em           DATETIME2(6)        NOT NULL CONSTRAINT df_tb_usuario_criado_em     DEFAULT (SYSUTCDATETIME()),
    atualizado_em       DATETIME2(6)        NOT NULL CONSTRAINT df_tb_usuario_atualizado_em DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT uk_tb_usuario_email UNIQUE (email),
    CONSTRAINT ck_tb_usuario_role  CHECK (role IN (
        'ALUNO',
        'PROFESSOR',
        'COORDENADOR',
        'SECRETARIA',
        'BIBLIOTECARIO',
        'FINANCEIRO',
        'RESPONSAVEL',
        'ADMIN'
    ))
);

CREATE INDEX ix_tb_usuario_role  ON tb_usuario(role);
CREATE INDEX ix_tb_usuario_ativo ON tb_usuario(ativo);

