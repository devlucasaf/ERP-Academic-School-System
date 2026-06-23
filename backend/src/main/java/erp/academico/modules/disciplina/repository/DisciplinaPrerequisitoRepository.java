package erp.academico.modules.disciplina.repository;

import erp.academico.modules.disciplina.model.DisciplinaPrerequisito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisciplinaPrerequisitoRepository extends JpaRepository<DisciplinaPrerequisito, UUID> {

    List<DisciplinaPrerequisito> findByDisciplinaId(UUID disciplinaId);

    Optional<DisciplinaPrerequisito> findByDisciplinaIdAndPrerequisitoId(UUID disciplinaId, UUID prerequisitoId);

    boolean existsByDisciplinaIdAndPrerequisitoId(UUID disciplinaId, UUID prerequisitoId);
}

