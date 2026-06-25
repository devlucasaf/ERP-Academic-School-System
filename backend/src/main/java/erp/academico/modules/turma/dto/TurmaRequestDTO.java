package erp.academico.modules.turma.dto;

import erp.academico.modules.turma.model.Turno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurmaRequestDTO {

    @NotBlank
    @Size(max = 50)
    private String codigo;

    @NotNull
    private UUID cursoId;

    @NotBlank
    @Size(max = 20)
    private String periodoLetivo;

    @NotBlank
    @Size(max = 50)
    private String serie;

    @Size(max = 50)
    private String sala;

    @NotNull
    private Turno turno;

    // --- OPCIONAL ---
    private UUID professorRegenteId;

    @NotNull
    @Positive
    private Integer capacidadeMaxima;

    private Boolean ativa;
}

