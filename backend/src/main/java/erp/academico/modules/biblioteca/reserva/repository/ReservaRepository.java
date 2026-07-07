package erp.academico.modules.biblioteca.reserva.repository;

import erp.academico.modules.biblioteca.reserva.model.Reserva;
import erp.academico.modules.biblioteca.reserva.model.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

    List<Reserva> findByLivroIdAndStatusOrderByPosicaoFilaAsc(UUID livroId, StatusReserva status);

    Optional<Reserva> findFirstByLivroIdAndStatusOrderByPosicaoFilaAsc(UUID livroId, StatusReserva status);

    List<Reserva> findByUsuarioIdOrderByDataReservaDesc(UUID usuarioId);

    boolean existsByLivroIdAndUsuarioIdAndStatus(UUID livroId, UUID usuarioId, StatusReserva status);

    long countByLivroIdAndStatus(UUID livroId, StatusReserva status);
}

