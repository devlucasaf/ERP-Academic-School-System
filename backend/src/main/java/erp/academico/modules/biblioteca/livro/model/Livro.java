package erp.academico.modules.biblioteca.livro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "livro")
public class Livro {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "isbn", length = 20, unique = true)
    private String isbn;

    @Column(name = "titulo", nullable = false, length = 300)
    private String titulo;

    @Column(name = "autor", nullable = false, length = 300)
    private String autor;

    @Column(name = "editora", length = 200)
    private String editora;

    @Column(name = "anoPublicacao")
    private Integer anoPublicacao;

    @Column(name = "edicao", length = 50)
    private String edicao;

    @Column(name = "paginas")
    private Integer paginas;

    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "sinopse", columnDefinition = "NVARCHAR(MAX)")
    private String sinopse;

    @Column(name = "capaUrl", length = 500)
    private String capaUrl;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizadoEm", nullable = false)
    private LocalDateTime atualizadoEm;
}

