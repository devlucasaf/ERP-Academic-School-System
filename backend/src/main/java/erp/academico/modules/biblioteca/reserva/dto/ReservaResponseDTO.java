package erp.academico.modules.biblioteca.reserva.dto;

import erp.academico.modules.biblioteca.reserva.model.StatusReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDTO {

    private UUID          id;
    private UUID          livroId;
    private String        livroTitulo;
    private UUID          usuarioId;
    private String        usuarioNome;
    private LocalDateTime dataReserva;
    private StatusReserva status;
    private Integer       posicaoFila;
}

