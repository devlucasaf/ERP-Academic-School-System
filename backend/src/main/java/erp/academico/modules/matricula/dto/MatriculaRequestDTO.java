package erp.academico.modules.matricula.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatriculaRequestDTO {

    @NotNull
    private UUID alunoId;

    @NotNull
    private UUID turmaId;

    private LocalDate dataMatricula;

    @Size(max = 1000)
    private String observacoes;
}

