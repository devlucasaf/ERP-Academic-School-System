package erp.academico.modules.frequencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrequenciaResponseDTO {

    private UUID        id;
    private UUID        aulaId;
    private LocalDate   dataAula;
    private UUID        alunoId;
    private String      alunoNome;
    private String      alunoMatriculaRA;
    private Boolean     presente;
    private String      justificativa;
}

