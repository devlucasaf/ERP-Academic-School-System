package erp.academico.modules.atividade.dto;

import erp.academico.modules.atividade.model.StatusEntrega;

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
public class EntregaAtividadeResponseDTO {

    private UUID            id;
    private UUID            atividadeId;
    private String          atividadeTitulo;
    private UUID            alunoId;
    private String          alunoNome;
    private String          arquivoUrl;
    private String          comentarioAluno;
    private LocalDateTime   dataEntrega;
    private BigDecimal      nota;
    private String          feedback;
    private StatusEntrega   status;
    private LocalDateTime   criadoEm;
    private LocalDateTime   atualizadoEm;
}

