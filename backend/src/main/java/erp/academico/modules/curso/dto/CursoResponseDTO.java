package erp.academico.modules.curso.dto;

import erp.academico.modules.curso.model.NivelCurso;
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
public class CursoResponseDTO {

    private UUID            id;
    private String          nome;
    private String          descricao;
    private NivelCurso      nivel;
    private Integer         duracaoSemestres;
    private Integer         cargaHorariaTotal;
    private Boolean         ativo;
    private LocalDateTime   criadoEm;
    private LocalDateTime   atualizadoEm;
}

