package erp.academico.modules.turma.model;

// --- IMPORTS ---
import erp.academico.modules.curso.model.Curso;
import erp.academico.modules.professor.model.Professor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "turma")
public class Turma {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cursoId", nullable = false,
            foreignKey = @ForeignKey(name = "fkTurmaCurso"))
    private Curso curso;

    @Column(name = "periodoLetivo", nullable = false, length = 20)
    private String periodoLetivo;

    @Column(name = "serie", nullable = false, length = 50)
    private String serie;

    @Column(name = "sala", length = 50)
    private String sala;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", nullable = false, length = 20)
    private Turno turno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professorRegenteId",
            foreignKey = @ForeignKey(name = "fkTurmaProfessorRegente"))
    private Professor professorRegente;

    @Column(name = "capacidadeMaxima", nullable = false)
    private Integer capacidadeMaxima;

    @Column(name = "ativa", nullable = false)
    private Boolean ativa;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizadoEm", nullable = false)
    private LocalDateTime atualizadoEm;
}