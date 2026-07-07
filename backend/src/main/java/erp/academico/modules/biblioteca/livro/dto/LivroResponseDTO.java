package erp.academico.modules.biblioteca.livro.dto;

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
public class LivroResponseDTO {

    private UUID          id;
    private String        isbn;
    private String        titulo;
    private String        autor;
    private String        editora;
    private Integer       anoPublicacao;
    private String        edicao;
    private Integer       paginas;
    private String        categoria;
    private String        sinopse;
    private String        capaUrl;
    private long          totalExemplares;
    private long          exemplaresDisponiveis;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}

