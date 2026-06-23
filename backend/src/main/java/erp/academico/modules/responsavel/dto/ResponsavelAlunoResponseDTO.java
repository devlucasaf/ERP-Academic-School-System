package erp.academico.modules.responsavel.dto;

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
public class ResponsavelAlunoResponseDTO {

    private UUID            id;
    private UUID            responsavelId;
    private UUID            alunoId;
    private String          alunoNome;
    private String          alunoMatriculaRA;
    private String          observacao;
    private LocalDateTime   criadoEm;
}

