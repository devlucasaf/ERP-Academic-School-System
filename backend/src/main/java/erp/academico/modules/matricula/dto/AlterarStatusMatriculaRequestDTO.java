package erp.academico.modules.matricula.dto;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlterarStatusMatriculaRequestDTO {

    @Size(max = 1000)
    private String observacoes;
}

