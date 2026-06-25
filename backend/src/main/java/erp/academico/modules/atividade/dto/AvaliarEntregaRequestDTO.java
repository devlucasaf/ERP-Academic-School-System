package erp.academico.modules.atividade.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
public class AvaliarEntregaRequestDTO {

    @NotNull
    @DecimalMin(value = "0.00", message = "Nota mínima é 0.")
    @DecimalMax(value = "10.00", message = "Nota máxima é 10.")
    private BigDecimal nota;

    @Size(max = 2000)
    private String feedback;
}

