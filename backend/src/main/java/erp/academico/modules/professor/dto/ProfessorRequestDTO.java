package erp.academico.modules.professor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessorRequestDTO {

    // --- DADOS DO USUÁRIO ASSOCIADO ---
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Size(max = 150)
    private String email;

    // --- SENHA OPCIONAL ---
    @Size(min = 6, max = 100)
    private String senha;

    @Size(max = 14)
    private String cpf;

    @Size(max = 20)
    private String telefone;

    private LocalDate dataNascimento;

    // --- DADOS PROFISSIONAIS ---
    @Size(max = 200)
    private String formacao;

    @Size(max = 150)
    private String areaAtuacao;

    @Positive(message = "Carga horária deve ser positiva.")
    private Integer cargaHorariaSemanal;

    @NotNull(message = "A data de admissão é obrigatória.")
    private LocalDate dataAdmissao;

    private Boolean ativo;
}

