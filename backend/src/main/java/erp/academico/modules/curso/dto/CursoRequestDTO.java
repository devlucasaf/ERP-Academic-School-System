package erp.academico.modules.curso.dto;

import erp.academico.modules.curso.model.NivelCurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CursoRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 150)
    private String nome;

    @Size(max = 1000)
    private String descricao;

    @NotNull(message = "O nível é obrigatório.")
    private NivelCurso nivel;

    @NotNull(message = "A duração (semestres) é obrigatória.")
    @Positive(message = "A duração deve ser positiva.")
    private Integer duracaoSemestres;

    @NotNull(message = "A carga horária total é obrigatória.")
    @Positive(message = "A carga horária deve ser positiva.")
    private Integer cargaHorariaTotal;

    private Boolean ativo;
}

