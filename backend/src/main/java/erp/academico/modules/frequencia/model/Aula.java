package erp.academico.modules.frequencia.model;

import erp.academico.modules.professor.model.Professor;
import erp.academico.modules.turma.model.TurmaDisciplina;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "aula")
public class Aula {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turmaDisciplinaId", nullable = false,
            foreignKey = @ForeignKey(name = "fkAulaTurmaDisciplina"))
    private TurmaDisciplina turmaDisciplina;

    @Column(name = "dataAula", nullable = false)
    private LocalDate dataAula;

    @Column(name = "conteudoMinistrado", length = 4000)
    private String conteudoMinistrado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professorId", nullable = false,
            foreignKey = @ForeignKey(name = "fkAulaProfessor"))
    private Professor professor;

    @CreationTimestamp
    @Column(name = "criadaEm", nullable = false, updatable = false)
    private LocalDateTime criadaEm;
}
