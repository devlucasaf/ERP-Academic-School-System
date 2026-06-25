package erp.academico.modules.nota.dto;

import erp.academico.modules.nota.model.PeriodoAvaliacao;
import erp.academico.modules.nota.model.TipoAvaliacao;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaRequestDTO {

    @NotNull
    private UUID alunoId;

    @NotNull
    private UUID turmaDisciplinaId;

    @NotNull
    private PeriodoAvaliacao periodoAvaliacao;

    @NotNull
    private TipoAvaliacao tipoAvaliacao;

    @NotNull
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "10.00")
    private BigDecimal valor;

    @Positive
    private BigDecimal peso;

    @Size(max = 1000)
    private String observacoes;
}

