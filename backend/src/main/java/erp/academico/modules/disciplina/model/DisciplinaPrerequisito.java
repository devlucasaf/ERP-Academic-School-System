package erp.academico.modules.disciplina.model;

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

/**
 * Autorrelacionamento de {@link Disciplina} representando dependência
 * de pré-requisito: para cursar {@code disciplina}, é preciso ter cursado
 * {@code prerequisito}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "tb_disciplina_prerequisito",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tb_disciplina_prerequisito",
                columnNames = {"disciplina_id", "prerequisito_id"})
)
public class DisciplinaPrerequisito {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplina_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tb_disc_prereq_disciplina"))
    private Disciplina disciplina;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prerequisito_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tb_disc_prereq_prerequisito"))
    private Disciplina prerequisito;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}

