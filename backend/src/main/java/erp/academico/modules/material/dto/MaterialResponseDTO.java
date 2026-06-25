package erp.academico.modules.material.dto;

import erp.academico.modules.material.model.TipoMaterial;

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
public class MaterialResponseDTO {

    private UUID            id;
    private UUID            turmaDisciplinaId;
    private String          titulo;
    private String          descricao;
    private TipoMaterial    tipo;
    private String          arquivoUrl;
    private String          linkUrl;
    private UUID            professorId;
    private String          professorNome;
    private LocalDateTime   criadoEm;
    private LocalDateTime   atualizadoEm;
}

