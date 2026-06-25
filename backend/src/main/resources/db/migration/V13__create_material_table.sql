CREATE TABLE material (
    id                  UNIQUEIDENTIFIER NOT NULL CONSTRAINT pkMaterial PRIMARY KEY,
    turmaDisciplinaId   UNIQUEIDENTIFIER NOT NULL,
    titulo              NVARCHAR(200)    NOT NULL,
    descricao           NVARCHAR(2000)   NULL,
    tipo                NVARCHAR(20)     NOT NULL,
    arquivoUrl          NVARCHAR(500)    NULL,
    linkUrl             NVARCHAR(500)    NULL,
    professorId         UNIQUEIDENTIFIER NOT NULL,
    criadoEm            DATETIME2(6)     NOT NULL CONSTRAINT dfMaterialCriadoEm     DEFAULT (SYSUTCDATETIME()),
    atualizadoEm        DATETIME2(6)     NOT NULL CONSTRAINT dfMaterialAtualizadoEm DEFAULT (SYSUTCDATETIME()),

    CONSTRAINT fkMaterialTurmaDisciplina FOREIGN KEY (turmaDisciplinaId) REFERENCES turmaDisciplina(id),
    CONSTRAINT fkMaterialProfessor       FOREIGN KEY (professorId)       REFERENCES professor(id),
    CONSTRAINT ckMaterialTipo            CHECK (tipo IN ('PDF','VIDEO','LINK','APRESENTACAO')),
    -- --- GARANTE QUE PELO MENOS UM ENTRE arquivoUrl E linkUrl ESTEJA PREENCHIDO ---
    CONSTRAINT ckMaterialUrlObrigatoria  CHECK (arquivoUrl IS NOT NULL OR linkUrl IS NOT NULL)
);

CREATE INDEX ixMaterialTurmaDisciplina ON material(turmaDisciplinaId);
CREATE INDEX ixMaterialProfessor       ON material(professorId);
CREATE INDEX ixMaterialTipo            ON material(tipo);

