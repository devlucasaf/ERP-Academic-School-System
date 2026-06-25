package erp.academico.modules.responsavel.model;

import erp.academico.modules.aluno.model.Aluno;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "responsavelAluno",
        uniqueConstraints = @UniqueConstraint(
                name = "ukResponsavelAluno",
                columnNames = {"responsavelId", "alunoId"})
)
public class ResponsavelAluno {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsavelId", nullable = false,
            foreignKey = @ForeignKey(name = "fkResponsavelAlunoResponsavel"))
    private Responsavel responsavel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alunoId", nullable = false,
            foreignKey = @ForeignKey(name = "fkResponsavelAlunoAluno"))
    private Aluno aluno;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}

