package erp.academico.modules.frequencia.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChamadaRequestDTO {

    @NotEmpty
    @Valid
    private List<ItemChamada> registros;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemChamada {

        @NotNull
        private UUID alunoId;

        @NotNull
        private Boolean presente;

        @Size(max = 1000)
        private String justificativa;
    }
}

