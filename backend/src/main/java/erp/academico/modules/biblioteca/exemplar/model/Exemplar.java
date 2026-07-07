package erp.academico.modules.biblioteca.exemplar.model;

import erp.academico.modules.biblioteca.livro.model.Livro;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exemplar")
public class Exemplar {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livroId", nullable = false,
            foreignKey = @ForeignKey(name = "fkExemplarLivro"))
    private Livro livro;

    @Column(name = "codigoBarras", nullable = false, unique = true, length = 50)
    private String codigoBarras;

    @Column(name = "localizacao", length = 50)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusExemplar status;

    @CreationTimestamp
    @Column(name = "criadoEm", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}

