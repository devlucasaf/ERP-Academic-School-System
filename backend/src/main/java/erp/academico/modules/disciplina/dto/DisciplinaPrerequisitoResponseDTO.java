package erp.academico.modules.disciplina.dto;

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
public class DisciplinaPrerequisitoResponseDTO {

    private UUID            id;
    private UUID            disciplinaId;
    private UUID            prerequisitoId;
    private String          prerequisitoCodigo;
    private String          prerequisitoNome;
    private LocalDateTime   criadoEm;
}

