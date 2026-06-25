package erp.academico.modules.disciplina.model;

import erp.academico.modules.curso.model.Curso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
@Table(name = "disciplina")
public class Disciplina {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "ementa", length = 4000)
    private String ementa;

    @Column(name = "cargaHoraria", nullable = false)
    private Integer cargaHoraria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cursoId", nullable = false,
            foreignKey = @ForeignKey(name = "fkDisciplinaCurso"))
    private Curso curso;

    @Column(name = "periodo", nullable = false)
    private Integer periodo;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizadoEm", nullable = false)
    private LocalDateTime atualizadoEm;
}

