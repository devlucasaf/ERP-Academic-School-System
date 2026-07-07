package erp.academico.modules.biblioteca.reserva.model;

import erp.academico.modules.biblioteca.livro.model.Livro;
import erp.academico.modules.usuario.model.Usuario;
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
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livroId", nullable = false,
            foreignKey = @ForeignKey(name = "fkReservaLivro"))
    private Livro livro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuarioId", nullable = false,
            foreignKey = @ForeignKey(name = "fkReservaUsuario"))
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "dataReserva", nullable = false, updatable = false)
    private LocalDateTime dataReserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusReserva status;

    @Column(name = "posicaoFila", nullable = false)
    private Integer posicaoFila;
}

