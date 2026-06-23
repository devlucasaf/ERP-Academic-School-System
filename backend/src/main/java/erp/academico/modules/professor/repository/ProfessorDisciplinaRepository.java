package erp.academico.modules.professor.repository;

import erp.academico.modules.professor.model.ProfessorDisciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfessorDisciplinaRepository extends JpaRepository<ProfessorDisciplina, UUID> {

    List<ProfessorDisciplina> findByProfessorId(UUID professorId);

    Optional<ProfessorDisciplina> findByProfessorIdAndDisciplinaId(UUID professorId, UUID disciplinaId);

    boolean existsByProfessorIdAndDisciplinaId(UUID professorId, UUID disciplinaId);
}

