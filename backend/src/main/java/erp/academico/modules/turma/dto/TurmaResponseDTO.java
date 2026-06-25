package erp.academico.modules.turma.dto;

import erp.academico.modules.turma.model.Turno;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurmaResponseDTO {

    private UUID            id;
    private String          codigo;
    private UUID            cursoId;
    private String          cursoNome;
    private String          periodoLetivo;
    private String          serie;
    private String          sala;
    private Turno           turno;
    private UUID            professorRegenteId;
    private String          professorRegenteNome;
    private Integer         capacidadeMaxima;
    private Boolean         ativa;
    private LocalDateTime   criadoEm;
    private LocalDateTime   atualizadoEm;
}

