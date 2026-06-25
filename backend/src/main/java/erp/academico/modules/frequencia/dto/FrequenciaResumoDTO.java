package erp.academico.modules.frequencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrequenciaResumoDTO {

    private UUID                            alunoId;
    private String                          alunoNome;
    private String                          alunoMatriculaRA;
    private List<FrequenciaPercentualDTO>   disciplinas;
}

