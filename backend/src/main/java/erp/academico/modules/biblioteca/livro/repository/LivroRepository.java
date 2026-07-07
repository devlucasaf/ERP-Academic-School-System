package erp.academico.modules.biblioteca.livro.repository;

import erp.academico.modules.biblioteca.livro.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LivroRepository extends JpaRepository<Livro, UUID> {

    Optional<Livro> findByIsbn(String isbn);

    @Query("""
            SELECT l FROM Livro l
             WHERE (:titulo    IS NULL OR LOWER(l.titulo)    LIKE LOWER(CONCAT('%', :titulo, '%')))
               AND (:autor     IS NULL OR LOWER(l.autor)     LIKE LOWER(CONCAT('%', :autor, '%')))
               AND (:categoria IS NULL OR LOWER(l.categoria) =    LOWER(:categoria))
               AND (:isbn      IS NULL OR l.isbn = :isbn)
            """)
    Page<Livro> buscar(@Param("titulo") String titulo,
                       @Param("autor") String autor,
                       @Param("categoria") String categoria,
                       @Param("isbn") String isbn,
                       Pageable pageable);
}

