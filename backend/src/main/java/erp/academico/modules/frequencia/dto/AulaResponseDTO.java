package erp.academico.modules.frequencia.dto;

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
public class AulaResponseDTO {

    private UUID            id;
    private UUID            turmaDisciplinaId;
    private UUID            turmaId;
    private String          turmaCodigo;
    private UUID            disciplinaId;
    private String          disciplinaNome;
    private UUID            professorId;
    private String          professorNome;
    private LocalDate       dataAula;
    private String          conteudoMinistrado;
    private Integer         totalAlunos;
    private LocalDateTime   criadaEm;
}

