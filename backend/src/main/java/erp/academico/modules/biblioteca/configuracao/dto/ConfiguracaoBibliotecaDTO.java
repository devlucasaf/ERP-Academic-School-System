package erp.academico.modules.biblioteca.configuracao.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracaoBibliotecaDTO {

    @NotNull @Positive
    private Integer prazoEmprestimoAluno;

    @NotNull @Positive
    private Integer prazoEmprestimoProfessor;

    @NotNull @Positive
    private Integer maxEmprestimosSimultaneos;

    @NotNull
    private Integer maxRenovacoes;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal valorMultaDia;
}

