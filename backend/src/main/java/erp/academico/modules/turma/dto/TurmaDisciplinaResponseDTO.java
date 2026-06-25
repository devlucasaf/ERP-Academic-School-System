package erp.academico.modules.turma.dto;

import erp.academico.modules.turma.model.DiaSemana;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurmaDisciplinaResponseDTO {

    private UUID            id;
    private UUID            turmaId;
    private UUID            disciplinaId;
    private String          disciplinaCodigo;
    private String          disciplinaNome;
    private UUID            professorId;
    private String          professorNome;
    private DiaSemana       diaSemana;
    private LocalTime       horarioInicio;
    private LocalTime       horarioFim;
    private LocalDateTime   criadoEm;
}

