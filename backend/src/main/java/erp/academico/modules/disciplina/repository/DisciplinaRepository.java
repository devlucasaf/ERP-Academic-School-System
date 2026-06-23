package erp.academico.modules.disciplina.repository;

import erp.academico.modules.disciplina.model.Disciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, UUID> {

    Optional<Disciplina> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    Page<Disciplina> findByCursoId(UUID cursoId, Pageable pageable);
}

