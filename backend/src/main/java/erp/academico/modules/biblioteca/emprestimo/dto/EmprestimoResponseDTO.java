package erp.academico.modules.biblioteca.emprestimo.dto;

import erp.academico.modules.biblioteca.emprestimo.model.StatusEmprestimo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoResponseDTO {

    private UUID              id;
    private UUID              exemplarId;
    private String            exemplarCodigoBarras;
    private UUID              livroId;
    private String            livroTitulo;
    private UUID              usuarioId;
    private String            usuarioNome;
    private LocalDateTime     dataEmprestimo;
    private LocalDateTime     dataDevolucaoPrevista;
    private LocalDateTime     dataDevolucaoEfetiva;
    private Integer           renovacoes;
    private StatusEmprestimo  status;
    private BigDecimal        valorMulta;
    private Integer           diasAtraso;
}

