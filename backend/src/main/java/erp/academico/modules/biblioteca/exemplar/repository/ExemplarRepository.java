package erp.academico.modules.biblioteca.exemplar.repository;

import erp.academico.modules.biblioteca.exemplar.model.Exemplar;
import erp.academico.modules.biblioteca.exemplar.model.StatusExemplar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExemplarRepository extends JpaRepository<Exemplar, UUID> {

    Optional<Exemplar> findByCodigoBarras(String codigoBarras);

    Page<Exemplar> findByLivroId(UUID livroId, Pageable pageable);

    List<Exemplar> findByLivroIdAndStatus(UUID livroId, StatusExemplar status);

    long countByLivroId(UUID livroId);

    long countByLivroIdAndStatus(UUID livroId, StatusExemplar status);

    boolean existsByCodigoBarras(String codigoBarras);
}

