package erp.academico.modules.biblioteca.livro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivroRequestDTO {

    @Size(max = 20)
    private String isbn;

    @NotBlank
    @Size(max = 300)
    private String titulo;

    @NotBlank
    @Size(max = 300)
    private String autor;

    @Size(max = 200)
    private String editora;

    private Integer anoPublicacao;

    @Size(max = 50)
    private String edicao;

    private Integer paginas;

    @Size(max = 100)
    private String categoria;

    private String sinopse;
}

