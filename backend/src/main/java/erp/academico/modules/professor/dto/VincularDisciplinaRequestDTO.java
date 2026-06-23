package erp.academico.modules.professor.dto;

import jakarta.validation.constraints.NotNull;
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
public class VincularDisciplinaRequestDTO {

    @NotNull(message = "O id da disciplina é obrigatório.")
    private UUID disciplinaId;
}

