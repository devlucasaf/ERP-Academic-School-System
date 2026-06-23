CREATE TABLE tb_disciplina (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_tb_disciplina PRIMARY KEY,
    codigo          NVARCHAR(20)     NOT NULL,
    nome            NVARCHAR(150)    NOT NULL,
    ementa          NVARCHAR(4000)   NULL,
    carga_horaria   INT              NOT NULL,
    curso_id        UNIQUEIDENTIFIER NOT NULL,
    periodo         INT              NOT NULL,
    ativo           BIT              NOT NULL CONSTRAINT df_tb_disciplina_ativo         DEFAULT (1),
    criado_em       DATETIME2(6)     NOT NULL CONSTRAINT df_tb_disciplina_criado_em     DEFAULT (SYSUTCDATETIME()),
    atualizado_em   DATETIME2(6)     NOT NULL CONSTRAINT df_tb_disciplina_atualizado_em DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT uk_tb_disciplina_codigo    UNIQUE (codigo),
    CONSTRAINT fk_tb_disciplina_curso     FOREIGN KEY (curso_id) REFERENCES tb_curso(id),
    CONSTRAINT ck_tb_disciplina_carga     CHECK (carga_horaria > 0),
    CONSTRAINT ck_tb_disciplina_periodo   CHECK (periodo > 0)
);

CREATE INDEX ix_tb_disciplina_curso   ON tb_disciplina(curso_id);
CREATE INDEX ix_tb_disciplina_periodo ON tb_disciplina(periodo);
CREATE INDEX ix_tb_disciplina_ativo   ON tb_disciplina(ativo);

CREATE TABLE tb_disciplina_prerequisito (
    id                  UNIQUEIDENTIFIER NOT NULL CONSTRAINT pk_tb_disc_prereq PRIMARY KEY,
    disciplina_id       UNIQUEIDENTIFIER NOT NULL,
    prerequisito_id     UNIQUEIDENTIFIER NOT NULL,
    criado_em           DATETIME2(6)     NOT NULL CONSTRAINT df_tb_disc_prereq_criado_em DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT uk_tb_disciplina_prerequisito          UNIQUE (disciplina_id, prerequisito_id),
    CONSTRAINT fk_tb_disc_prereq_disciplina           FOREIGN KEY (disciplina_id)   REFERENCES tb_disciplina(id),
    CONSTRAINT fk_tb_disc_prereq_prerequisito         FOREIGN KEY (prerequisito_id) REFERENCES tb_disciplina(id),
    CONSTRAINT ck_tb_disc_prereq_nao_self             CHECK (disciplina_id <> prerequisito_id)
);

CREATE INDEX ix_tb_disc_prereq_disciplina   ON tb_disciplina_prerequisito(disciplina_id);
CREATE INDEX ix_tb_disc_prereq_prerequisito ON tb_disciplina_prerequisito(prerequisito_id);

ALTER TABLE tb_professor_disciplina
    ADD CONSTRAINT fk_tb_prof_disc_disciplina
        FOREIGN KEY (disciplina_id) REFERENCES tb_disciplina(id);

