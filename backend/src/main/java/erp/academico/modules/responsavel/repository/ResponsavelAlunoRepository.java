package erp.academico.modules.responsavel.repository;

import erp.academico.modules.responsavel.model.ResponsavelAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResponsavelAlunoRepository extends JpaRepository<ResponsavelAluno, UUID> {

    List<ResponsavelAluno> findByResponsavelId(UUID responsavelId);

    List<ResponsavelAluno> findByAlunoId(UUID alunoId);

    Optional<ResponsavelAluno> findByResponsavelIdAndAlunoId(UUID responsavelId, UUID alunoId);

    boolean existsByResponsavelIdAndAlunoId(UUID responsavelId, UUID alunoId);
}

