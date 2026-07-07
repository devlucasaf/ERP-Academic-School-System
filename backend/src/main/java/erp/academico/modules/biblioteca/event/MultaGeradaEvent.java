package erp.academico.modules.biblioteca.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// --- EVENTO DISPARADO QUANDO UMA MULTA É GERADA ---
// --- SERÁ CONSUMIDO PELO MÓDULO FINANCEIRO (INTEGRAÇÃO FUTURA) ---
@Getter
@RequiredArgsConstructor
public class MultaGeradaEvent {

    private final UUID          multaId;
    private final UUID          emprestimoId;
    private final UUID          usuarioId;
    private final BigDecimal    valor;
    private final Integer       diasAtraso;
    private final LocalDateTime geradaEm;
}

