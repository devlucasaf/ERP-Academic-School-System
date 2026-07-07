package erp.academico.modules.biblioteca.multa.model;

import erp.academico.modules.biblioteca.emprestimo.model.Emprestimo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// --- MULTA ASSOCIADA A UM EMPRÉSTIMO ATRASADO ---
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "multa")
public class Multa {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emprestimoId", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fkMultaEmprestimo"))
    private Emprestimo emprestimo;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "diasAtraso", nullable = false)
    private Integer diasAtraso;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusMulta status;

    @CreationTimestamp
    @Column(name = "geradaEm", nullable = false, updatable = false)
    private LocalDateTime geradaEm;

    @Column(name = "pagaEm")
    private LocalDateTime pagaEm;
}

