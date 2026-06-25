package erp.academico.modules.atividade.dto;

import erp.academico.modules.atividade.model.TipoAtividade;

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
public class AtividadeResponseDTO {

    private UUID            id;
    private UUID            turmaDisciplinaId;
    private String          titulo;
    private String          descricao;
    private TipoAtividade   tipo;
    private LocalDateTime   dataPostagem;
    private LocalDateTime   dataEntrega;
    private BigDecimal      valorMaximo;
    private UUID            professorId;
    private String          professorNome;
    private Boolean         ativa;
    private LocalDateTime   criadoEm;
    private LocalDateTime   atualizadoEm;
}

