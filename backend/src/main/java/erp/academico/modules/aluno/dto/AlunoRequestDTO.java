package erp.academico.modules.aluno.dto;

import erp.academico.modules.aluno.model.StatusAluno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class AlunoRequestDTO {

    // --- DADOS DO USUÁRIO ASSOCIADO ---
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, max = 100)
    private String senha;

    @Size(max = 14)
    private String cpf;

    @Size(max = 20)
    private String telefone;

    private LocalDate dataNascimento;

    // --- DADOS ACADÊMICOS ---
    @NotBlank(message = "A matrícula (RA) é obrigatória.")
    @Size(max = 30)
    private String matriculaRA;

    @NotNull(message = "A data de ingresso é obrigatória.")
    private LocalDate dataIngresso;

    @NotNull(message = "O status é obrigatório.")
    private StatusAluno status;

    private UUID turmaAtualId;

    @Size(max = 1000)
    private String observacoes;
}

