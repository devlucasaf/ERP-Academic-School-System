package erp.academico.modules.biblioteca.multa.dto;

import erp.academico.modules.biblioteca.multa.model.StatusMulta;
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
public class MultaResponseDTO {

    private UUID          id;
    private UUID          emprestimoId;
    private UUID          usuarioId;
    private String        usuarioNome;
    private String        livroTitulo;
    private BigDecimal    valor;
    private Integer       diasAtraso;
    private StatusMulta   status;
    private LocalDateTime geradaEm;
    private LocalDateTime pagaEm;
}

