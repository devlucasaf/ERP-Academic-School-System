package erp.academico.modules.turma.dto;

import erp.academico.modules.turma.model.DiaSemana;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VincularDisciplinaTurmaRequestDTO {

    @NotNull private UUID       disciplinaId;
    @NotNull private UUID       professorId;
    @NotNull private DiaSemana  diaSemana;
    @NotNull private LocalTime  horarioInicio;
    @NotNull private LocalTime  horarioFim;
}

