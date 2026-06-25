package erp.academico.modules.atividade.model;

import erp.academico.modules.professor.model.Professor;
import erp.academico.modules.turma.model.TurmaDisciplina;

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
@Table(name = "atividade")
public class Atividade {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turmaDisciplinaId", nullable = false,
            foreignKey = @ForeignKey(name = "fkAtividadeTurmaDisciplina"))
    private TurmaDisciplina turmaDisciplina;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", length = 4000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoAtividade tipo;

    @Column(name = "dataPostagem", nullable = false)
    private LocalDateTime dataPostagem;

    @Column(name = "dataEntrega", nullable = false)
    private LocalDateTime dataEntrega;

    @Column(name = "valorMaximo", nullable = false, precision = 5, scale = 2)
    private BigDecimal valorMaximo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professorId", nullable = false,
            foreignKey = @ForeignKey(name = "fkAtividadeProfessor"))
    private Professor professor;

    @Column(name = "ativa", nullable = false)
    private Boolean ativa;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizadoEm", nullable = false)
    private LocalDateTime atualizadoEm;
}

