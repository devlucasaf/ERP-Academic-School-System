package erp.academico.modules.atividade.repository;

import erp.academico.modules.atividade.model.Atividade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, UUID> {

    Page<Atividade> findByTurmaDisciplinaId(UUID turmaDisciplinaId, Pageable pageable);
}

