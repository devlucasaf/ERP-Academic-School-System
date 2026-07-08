package erp.academico.modules.responsavel.dto;

import erp.academico.modules.responsavel.model.Parentesco;

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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsavelRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Size(max = 150)
    private String email;

    // --- SENHA OPCIONAL: SE VAZIA, O SISTEMA GERA UMA TEMPORÁRIA E ENVIA POR E-MAIL ---
    @Size(min = 6, max = 100)
    private String senha;

    @Size(max = 14)
    private String cpf;

    @Size(max = 20)
    private String telefone;

    private LocalDate dataNascimento;

    @NotNull(message = "O parentesco é obrigatório.")
    private Parentesco parentesco;
}

