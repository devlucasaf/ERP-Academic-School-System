package erp.academico.modules.aluno.repository;

import erp.academico.modules.aluno.model.Aluno;
import erp.academico.modules.aluno.model.StatusAluno;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, UUID> {

    Optional<Aluno> findByMatriculaRA(String matriculaRA);

    boolean existsByMatriculaRA(String matriculaRA);

    Optional<Aluno> findByUsuarioId(UUID usuarioId);

    Page<Aluno> findByStatus(StatusAluno status, Pageable pageable);

    Page<Aluno> findByTurmaAtualId(UUID turmaAtualId, Pageable pageable);
}
