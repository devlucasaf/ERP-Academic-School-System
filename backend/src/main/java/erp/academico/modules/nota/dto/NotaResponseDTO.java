package erp.academico.modules.nota.dto;

import erp.academico.modules.nota.model.PeriodoAvaliacao;
import erp.academico.modules.nota.model.TipoAvaliacao;

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
public class NotaResponseDTO {

    private UUID                id;
    private UUID                alunoId;
    private String              alunoNome;
    private String              alunoMatriculaRA;
    private UUID                turmaDisciplinaId;
    private UUID                turmaId;
    private String              turmaCodigo;
    private UUID                disciplinaId;
    private String              disciplinaNome;
    private UUID                professorId;
    private String              professorNome;
    private PeriodoAvaliacao    periodoAvaliacao;
    private TipoAvaliacao       tipoAvaliacao;
    private BigDecimal          valor;
    private BigDecimal          peso;
    private String              observacoes;
    private UUID                lancadaPorId;
    private String              lancadaPorNome;
    private LocalDateTime       lancadaEm;
    private LocalDateTime       atualizadaEm;
}

