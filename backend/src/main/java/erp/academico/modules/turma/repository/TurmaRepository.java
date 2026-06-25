package erp.academico.modules.turma.repository;

import erp.academico.modules.turma.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, UUID> {

    Optional<Turma> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    Page<Turma> findByCursoId(UUID cursoId, Pageable pageable);

    Page<Turma> findByPeriodoLetivo(String periodoLetivo, Pageable pageable);
}
