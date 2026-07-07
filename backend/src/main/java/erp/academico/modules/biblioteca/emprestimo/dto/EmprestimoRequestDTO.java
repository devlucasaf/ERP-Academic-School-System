package erp.academico.modules.biblioteca.emprestimo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

// --- REGISTRA UM NOVO EMPRÉSTIMO ---
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoRequestDTO {

    // --- IDENTIFICAÇÃO DO EXEMPLAR VIA CÓDIGO DE BARRAS OU ID ---
    private UUID exemplarId;
    private String codigoBarras;

    @NotNull
    private UUID usuarioId;
}

