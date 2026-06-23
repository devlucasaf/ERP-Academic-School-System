package erp.academico.modules.disciplina.dto;

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
public class VincularPrerequisitoRequestDTO {

    @NotNull(message = "O id da disciplina pré-requisito é obrigatório.")
    private UUID prerequisitoId;
}

