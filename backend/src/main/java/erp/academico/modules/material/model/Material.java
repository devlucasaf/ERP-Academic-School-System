package erp.academico.modules.material.model;

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

import java.time.LocalDateTime;
import java.util.UUID;

// --- MATERIAL DIDÁTICO ASSOCIADO A UMA TURMA/DISCIPLINA (ARQUIVO OU LINK EXTERNO) ---
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "material")
public class Material {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turmaDisciplinaId", nullable = false,
            foreignKey = @ForeignKey(name = "fkMaterialTurmaDisciplina"))
    private TurmaDisciplina turmaDisciplina;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", length = 2000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMaterial tipo;

    @Column(name = "arquivoUrl", length = 500)
    private String arquivoUrl;

    @Column(name = "linkUrl", length = 500)
    private String linkUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professorId", nullable = false,
            foreignKey = @ForeignKey(name = "fkMaterialProfessor"))
    private Professor professor;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizadoEm", nullable = false)
    private LocalDateTime atualizadoEm;
}

