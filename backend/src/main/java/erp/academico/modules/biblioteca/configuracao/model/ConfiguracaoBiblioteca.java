package erp.academico.modules.biblioteca.configuracao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Table(name = "configuracaoBiblioteca")
public class ConfiguracaoBiblioteca {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "prazoEmprestimoAluno", nullable = false)
    private Integer prazoEmprestimoAluno;

    @Column(name = "prazoEmprestimoProfessor", nullable = false)
    private Integer prazoEmprestimoProfessor;

    @Column(name = "maxEmprestimosSimultaneos", nullable = false)
    private Integer maxEmprestimosSimultaneos;

    @Column(name = "maxRenovacoes", nullable = false)
    private Integer maxRenovacoes;

    @Column(name = "valorMultaDia", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMultaDia;

    @UpdateTimestamp
    @Column(name = "atualizadoEm", nullable = false)
    private LocalDateTime atualizadoEm;
}

