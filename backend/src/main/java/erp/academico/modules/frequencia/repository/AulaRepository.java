package erp.academico.modules.frequencia.repository;

import erp.academico.modules.frequencia.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AulaRepository extends JpaRepository<Aula, UUID> {

    List<Aula> findByTurmaDisciplinaId(UUID turmaDisciplinaId);
}

