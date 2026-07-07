package erp.academico.modules.biblioteca.exemplar.dto;

import erp.academico.modules.biblioteca.exemplar.model.StatusExemplar;

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
public class ExemplarResponseDTO {

    private UUID           id;
    private UUID           livroId;
    private String         livroTitulo;
    private String         codigoBarras;
    private String         localizacao;
    private StatusExemplar status;
    private LocalDateTime  criadoEm;
}

