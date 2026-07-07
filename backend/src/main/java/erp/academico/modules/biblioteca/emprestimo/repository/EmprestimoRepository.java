package erp.academico.modules.biblioteca.emprestimo.repository;

import erp.academico.modules.biblioteca.emprestimo.model.Emprestimo;
import erp.academico.modules.biblioteca.emprestimo.model.StatusEmprestimo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, UUID> {

    long countByUsuarioIdAndStatus(UUID usuarioId, StatusEmprestimo status);

    Page<Emprestimo> findByUsuarioId(UUID usuarioId, Pageable pageable);

    List<Emprestimo> findByUsuarioIdAndStatus(UUID usuarioId, StatusEmprestimo status);

    List<Emprestimo> findByStatusAndDataDevolucaoPrevistaBefore(StatusEmprestimo status, LocalDateTime data);
}

