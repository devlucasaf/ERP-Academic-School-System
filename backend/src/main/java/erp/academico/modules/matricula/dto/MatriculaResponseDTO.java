package erp.academico.modules.matricula.dto;

import erp.academico.modules.matricula.model.StatusMatricula;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatriculaResponseDTO {

    private UUID            id;
    private UUID            alunoId;
    private String          alunoNome;
    private String          alunoMatriculaRA;
    private UUID            turmaId;
    private String          turmaCodigo;
    private String          turmaPeriodoLetivo;
    private LocalDate       dataMatricula;
    private StatusMatricula status;
    private String          observacoes;
    private UUID            criadaPorId;
    private String          criadaPorNome;
    private LocalDateTime   criadoEm;
    private LocalDateTime   atualizadoEm;
}

