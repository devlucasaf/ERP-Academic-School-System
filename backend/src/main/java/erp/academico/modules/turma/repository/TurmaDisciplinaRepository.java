package erp.academico.modules.turma.repository;

import erp.academico.modules.turma.model.TurmaDisciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TurmaDisciplinaRepository extends JpaRepository<TurmaDisciplina, UUID> {

    List<TurmaDisciplina> findByTurmaId(UUID turmaId);

    boolean existsByTurmaIdAndDisciplinaId(UUID turmaId, UUID disciplinaId);

    Optional<TurmaDisciplina> findByTurmaIdAndDisciplinaId(UUID turmaId, UUID disciplinaId);
}

