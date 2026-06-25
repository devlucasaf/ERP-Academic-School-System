package erp.academico.modules.professor.model;

import erp.academico.modules.usuario.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "professor")
public class Professor {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuarioId", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fkProfessorUsuario"))
    private Usuario usuario;

    @Column(name = "formacao", length = 200)
    private String formacao;

    @Column(name = "areaAtuacao", length = 150)
    private String areaAtuacao;

    @Column(name = "cargaHorariaSemanal")
    private Integer cargaHorariaSemanal;

    @Column(name = "dataAdmissao", nullable = false)
    private LocalDate dataAdmissao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizadoEm", nullable = false)
    private LocalDateTime atualizadoEm;
}

