CREATE TABLE atividade (
    id                  UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkAtividade PRIMARY KEY,
    turmaDisciplinaId   UNIQUEIDENTIFIER NOT NULL,
    titulo              NVARCHAR(200)    NOT NULL,
    descricao           NVARCHAR(4000)   NULL,
    tipo                NVARCHAR(20)     NOT NULL,
    dataPostagem        DATETIME2(6)     NOT NULL,
    dataEntrega         DATETIME2(6)     NOT NULL,
    valorMaximo         DECIMAL(5,2)     NOT NULL,
    professorId         UNIQUEIDENTIFIER NOT NULL,
    ativa               BIT              NOT NULL CONSTRAINT dfAtividadeAtiva        DEFAULT (1),
    criadoEm            DATETIME2(6)     NOT NULL CONSTRAINT dfAtividadeCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfAtividadeAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT fkAtividadeTurmaDisciplina FOREIGN KEY (turmaDisciplinaId) REFERENCES turmaDisciplina(id),
    CONSTRAINT fkAtividadeProfessor       FOREIGN KEY (professorId)       REFERENCES professor(id),
    CONSTRAINT ckAtividadeTipo            CHECK (tipo IN ('TAREFA','TRABALHO','PROJETO','PROVA')),
    CONSTRAINT ckAtividadeValorMaximo     CHECK (valorMaximo > 0),
    CONSTRAINT ckAtividadeDatas           CHECK (dataEntrega >= dataPostagem)
);

CREATE INDEX ixAtividadeTurmaDisciplina ON atividade(turmaDisciplinaId);
CREATE INDEX ixAtividadeProfessor       ON atividade(professorId);
CREATE INDEX ixAtividadeAtiva           ON atividade(ativa);
CREATE INDEX ixAtividadeDataEntrega     ON atividade(dataEntrega);

CREATE TABLE entregaAtividade (
    id              UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkEntregaAtividade PRIMARY KEY,
    atividadeId     UNIQUEIDENTIFIER NOT NULL,
    alunoId         UNIQUEIDENTIFIER NOT NULL,
    arquivoUrl      NVARCHAR(500)    NULL,
    comentarioAluno NVARCHAR(2000)   NULL,
    dataEntrega     DATETIME2(6)     NOT NULL,
    nota            DECIMAL(5,2)     NULL,
    feedback        NVARCHAR(2000)   NULL,
    status          NVARCHAR(20)     NOT NULL,
    criadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfEntregaAtividadeCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm    DATETIME2(6)     NOT NULL CONSTRAINT dfEntregaAtividadeAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT ukEntregaAtividadeAtividadeAluno UNIQUE (atividadeId, alunoId),
    CONSTRAINT fkEntregaAtividadeAtividade      FOREIGN KEY (atividadeId) REFERENCES atividade(id),
    CONSTRAINT fkEntregaAtividadeAluno          FOREIGN KEY (alunoId)     REFERENCES aluno(id),
    CONSTRAINT ckEntregaAtividadeStatus         CHECK (status IN ('PENDENTE','ENTREGUE','AVALIADA','ATRASADA')),
    CONSTRAINT ckEntregaAtividadeNota           CHECK (nota IS NULL OR (nota >= 0 AND nota <= 10))
);

CREATE INDEX ixEntregaAtividadeAtividade ON entregaAtividade(atividadeId);
CREATE INDEX ixEntregaAtividadeAluno     ON entregaAtividade(alunoId);
CREATE INDEX ixEntregaAtividadeStatus    ON entregaAtividade(status);

