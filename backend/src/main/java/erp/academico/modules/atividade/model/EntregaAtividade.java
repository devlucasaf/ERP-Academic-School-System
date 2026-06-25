package erp.academico.modules.atividade.model;

import erp.academico.modules.aluno.model.Aluno;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "entregaAtividade",
        uniqueConstraints = @UniqueConstraint(
                name = "ukEntregaAtividadeAtividadeAluno",
                columnNames = {"atividadeId", "alunoId"}
        )
)
public class EntregaAtividade {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atividadeId", nullable = false,
            foreignKey = @ForeignKey(name = "fkEntregaAtividadeAtividade"))
    private Atividade atividade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alunoId", nullable = false,
            foreignKey = @ForeignKey(name = "fkEntregaAtividadeAluno"))
    private Aluno aluno;

    @Column(name = "arquivoUrl", length = 500)
    private String arquivoUrl;

    @Column(name = "comentarioAluno", length = 2000)
    private String comentarioAluno;

    @Column(name = "dataEntrega", nullable = false)
    private LocalDateTime dataEntrega;

    @Column(name = "nota", precision = 5, scale = 2)
    private BigDecimal nota;

    @Column(name = "feedback", length = 2000)
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusEntrega status;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizadoEm", nullable = false)
    private LocalDateTime atualizadoEm;
}

