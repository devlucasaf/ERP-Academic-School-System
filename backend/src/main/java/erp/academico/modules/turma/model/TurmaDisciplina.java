package erp.academico.modules.turma.model;

// --- IMPORTS ---
import erp.academico.modules.disciplina.model.Disciplina;
import erp.academico.modules.professor.model.Professor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "turmaDisciplina",
        uniqueConstraints = @UniqueConstraint(
                name = "ukTurmaDisciplina",
                columnNames = {"turmaId", "disciplinaId"}
        )
)
public class TurmaDisciplina {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turmaId", nullable = false,
            foreignKey = @ForeignKey(name = "fkTurmaDisciplinaTurma"))
    private Turma turma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplinaId", nullable = false,
            foreignKey = @ForeignKey(name = "fkTurmaDisciplinaDisciplina"))
    private Disciplina disciplina;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professorId", nullable = false,
            foreignKey = @ForeignKey(name = "fkTurmaDisciplinaProfessor"))
    private Professor professor;

    @Enumerated(EnumType.STRING)
    @Column(name = "diaSemana", nullable = false, length = 20)
    private DiaSemana diaSemana;

    @Column(name = "horarioInicio", nullable = false)
    private LocalTime horarioInicio;

    @Column(name = "horarioFim", nullable = false)
    private LocalTime horarioFim;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}
