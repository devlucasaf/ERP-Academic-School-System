package erp.academico.modules.biblioteca.exemplar.dto;

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
public class ExemplarRequestDTO {

    @NotNull
    private UUID livroId;

    @Size(max = 50)
    private String codigoBarras;

    @Size(max = 50)
    private String localizacao;
}

