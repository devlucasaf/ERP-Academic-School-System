package erp.academico.modules.biblioteca.emprestimo.model;

import erp.academico.modules.biblioteca.exemplar.model.Exemplar;
import erp.academico.modules.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

// --- EMPRÉSTIMO DE UM EXEMPLAR PARA UM USUÁRIO ---
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "emprestimo")
public class Emprestimo {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exemplarId", nullable = false,
            foreignKey = @ForeignKey(name = "fkEmprestimoExemplar"))
    private Exemplar exemplar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuarioId", nullable = false,
            foreignKey = @ForeignKey(name = "fkEmprestimoUsuario"))
    private Usuario usuario;

    @Column(name = "dataEmprestimo", nullable = false)
    private LocalDateTime dataEmprestimo;

    @Column(name = "dataDevolucaoPrevista", nullable = false)
    private LocalDateTime dataDevolucaoPrevista;

    @Column(name = "dataDevolucaoEfetiva")
    private LocalDateTime dataDevolucaoEfetiva;

    @Column(name = "renovacoes", nullable = false)
    private Integer renovacoes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusEmprestimo status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "criadoPorId", nullable = false,
            foreignKey = @ForeignKey(name = "fkEmprestimoCriadoPor"))
    private Usuario criadoPor;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}

