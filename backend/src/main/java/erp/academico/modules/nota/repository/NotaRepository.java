package erp.academico.modules.nota.repository;

import erp.academico.modules.nota.model.Nota;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotaRepository extends JpaRepository<Nota, UUID> {

    List<Nota> findByTurmaDisciplina_TurmaIdAndTurmaDisciplina_DisciplinaId(UUID turmaId, UUID disciplinaId);

    List<Nota> findByAlunoId(UUID alunoId);

    List<Nota> findByAlunoIdAndTurmaDisciplina_TurmaPeriodoLetivo(UUID alunoId, String periodoLetivo);
}

