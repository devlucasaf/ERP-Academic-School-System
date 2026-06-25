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
public class DisciplinaResponseDTO {

    private UUID            id;
    private String          codigo;
    private String          nome;
    private String          ementa;
    private Integer         cargaHoraria;
    private UUID            cursoId;
    private String          cursoNome;
    private Integer         periodo;
    private Boolean         ativo;
    private LocalDateTime   criadoEm;
    private LocalDateTime   atualizadoEm;
}

