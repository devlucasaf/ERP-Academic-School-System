package erp.academico.modules.disciplina.dto;

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
public class DisciplinaRequestDTO {

    @NotBlank(message = "O código é obrigatório.")
    @Size(max = 20)
    private String codigo;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 150)
    private String nome;

    @Size(max = 4000)
    private String ementa;

    @NotNull(message = "A carga horária é obrigatória.")
    @Positive(message = "A carga horária deve ser positiva.")
    private Integer cargaHoraria;

    @NotNull(message = "O curso é obrigatório.")
    private UUID cursoId;

    @NotNull(message = "O período é obrigatório.")
    @Positive(message = "O período deve ser positivo.")
    private Integer periodo;

    private Boolean ativo;
}

