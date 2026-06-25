package erp.academico.modules.atividade.repository;

import erp.academico.modules.atividade.model.EntregaAtividade;
import erp.academico.modules.atividade.model.StatusEntrega;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntregaAtividadeRepository extends JpaRepository<EntregaAtividade, UUID> {

    Optional<EntregaAtividade> findByAtividadeIdAndAlunoId(UUID atividadeId, UUID alunoId);

    boolean existsByAtividadeIdAndAlunoId(UUID atividadeId, UUID alunoId);

    List<EntregaAtividade> findByAtividadeId(UUID atividadeId);

    Page<EntregaAtividade> findByAlunoId(UUID alunoId, Pageable pageable);

    Page<EntregaAtividade> findByAlunoIdAndStatus(UUID alunoId, StatusEntrega status, Pageable pageable);
}

