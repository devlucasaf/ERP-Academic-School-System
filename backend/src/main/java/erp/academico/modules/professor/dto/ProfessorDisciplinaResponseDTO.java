package erp.academico.modules.professor.dto;

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
public class ProfessorDisciplinaResponseDTO {

    private UUID            id;
    private UUID            professorId;
    private UUID            disciplinaId;
    private LocalDateTime   criadoEm;
}

