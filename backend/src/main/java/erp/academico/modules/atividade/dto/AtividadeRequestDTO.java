package erp.academico.modules.atividade.dto;

import erp.academico.modules.atividade.model.TipoAtividade;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtividadeRequestDTO {

    @NotNull
    private UUID turmaDisciplinaId;

    @NotNull
    @Size(min = 3, max = 200)
    private String titulo;

    @Size(max = 4000)
    private String descricao;

    @NotNull
    private TipoAtividade tipo;

    @NotNull
    private LocalDateTime dataEntrega;

    @NotNull
    @DecimalMin(value = "0.01", message = "Valor máximo deve ser maior que zero.")
    private BigDecimal valorMaximo;

    private Boolean ativa;
}

