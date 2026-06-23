package erp.academico.modules.responsavel.dto;

import jakarta.validation.constraints.NotNull;
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
public class VincularAlunoRequestDTO {

    @NotNull(message = "O id do aluno é obrigatório.")
    private UUID alunoId;

    @Size(max = 500)
    private String observacao;
}

