package erp.academico.modules.curso.repository;

import erp.academico.modules.curso.model.Curso;
import erp.academico.modules.curso.model.NivelCurso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CursoRepository extends JpaRepository<Curso, UUID> {

    Page<Curso> findByNivel(NivelCurso nivel, Pageable pageable);

    Page<Curso> findByAtivo(Boolean ativo, Pageable pageable);
}

