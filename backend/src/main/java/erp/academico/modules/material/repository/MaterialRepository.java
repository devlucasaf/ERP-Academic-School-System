package erp.academico.modules.material.repository;

import erp.academico.modules.material.model.Material;
import erp.academico.modules.material.model.TipoMaterial;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {

    Page<Material> findByTurmaDisciplinaId(UUID turmaDisciplinaId, Pageable pageable);

    Page<Material> findByTurmaDisciplinaIdAndTipo(UUID turmaDisciplinaId, TipoMaterial tipo, Pageable pageable);

    @Query("""
            SELECT m FROM Material m
             WHERE m.turmaDisciplina.turma.id = :turmaId
               AND m.turmaDisciplina.disciplina.id = :disciplinaId
            """)
    Page<Material> findByTurmaIdAndDisciplinaId(@Param("turmaId") UUID turmaId,
                                                @Param("disciplinaId") UUID disciplinaId,
                                                Pageable pageable);
}

