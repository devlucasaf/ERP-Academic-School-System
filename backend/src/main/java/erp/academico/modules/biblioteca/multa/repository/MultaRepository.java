package erp.academico.modules.biblioteca.multa.repository;

import erp.academico.modules.biblioteca.multa.model.Multa;
import erp.academico.modules.biblioteca.multa.model.StatusMulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MultaRepository extends JpaRepository<Multa, UUID> {

    Page<Multa> findByStatus(StatusMulta status, Pageable pageable);

    List<Multa> findByEmprestimoUsuarioIdAndStatus(UUID usuarioId, StatusMulta status);

    @Query("""
            SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END
              FROM Multa m
             WHERE m.emprestimo.usuario.id = :usuarioId
               AND m.status = erp.academico.modules.biblioteca.multa.model.StatusMulta.PENDENTE
            """)
    boolean existePendenteDoUsuario(@Param("usuarioId") UUID usuarioId);
}

