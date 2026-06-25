package erp.academico.modules.material.dto;

import erp.academico.modules.material.model.TipoMaterial;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialRequestDTO {

    @NotNull
    private UUID turmaDisciplinaId;

    @NotNull
    @Size(min = 3, max = 200)
    private String titulo;

    @Size(max = 2000)
    private String descricao;

    @NotNull
    private TipoMaterial tipo;

    @Size(max = 500)
    private String linkUrl;
}

