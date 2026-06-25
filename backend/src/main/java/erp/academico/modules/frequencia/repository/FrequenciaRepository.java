package erp.academico.modules.frequencia.repository;

import erp.academico.modules.frequencia.model.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FrequenciaRepository extends JpaRepository<Frequencia, UUID> {

    List<Frequencia> findByAulaId(UUID aulaId);

    Optional<Frequencia> findByAulaIdAndAlunoId(UUID aulaId, UUID alunoId);

    @Query("""
            select f from Frequencia f
            where f.aluno.id = :alunoId
              and f.aula.turmaDisciplina.disciplina.id = :disciplinaId
            """)
    List<Frequencia> findByAlunoAndDisciplina(@Param("alunoId") UUID alunoId,
                                              @Param("disciplinaId") UUID disciplinaId);

    List<Frequencia> findByAlunoId(UUID alunoId);
}

