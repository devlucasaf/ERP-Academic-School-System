package erp.academico.modules.frequencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrequenciaPercentualDTO {

    private UUID        alunoId;
    private String      alunoNome;
    private UUID        disciplinaId;
    private String      disciplinaNome;
    private long        totalAulas;
    private long        presencas;
    private long        faltas;
    private BigDecimal  percentual;
}

