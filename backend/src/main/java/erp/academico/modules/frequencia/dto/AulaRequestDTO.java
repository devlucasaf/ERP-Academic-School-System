package erp.academico.modules.frequencia.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AulaRequestDTO {

    @NotNull
    private UUID turmaDisciplinaId;

    @NotNull
    private LocalDate dataAula;

    @Size(max = 4000)
    private String conteudoMinistrado;

    private UUID professorId;
}

