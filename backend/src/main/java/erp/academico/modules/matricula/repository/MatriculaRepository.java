package erp.academico.modules.matricula.repository;

import erp.academico.modules.matricula.model.Matricula;
import erp.academico.modules.matricula.model.StatusMatricula;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, UUID> {

    Page<Matricula> findByAlunoId(UUID alunoId, Pageable pageable);

    boolean existsByAlunoIdAndTurmaPeriodoLetivoAndStatus(UUID alunoId, String periodoLetivo, StatusMatricula status);

    long countByTurmaIdAndStatus(UUID turmaId, StatusMatricula status);

    // --- MATRÍCULAS DE UMA TURMA EM UM STATUS (USADO PARA GERAR FREQUÊNCIAS AO CRIAR AULA) ---
    List<Matricula> findByTurmaIdAndStatus(UUID turmaId, StatusMatricula status);

    // --- VERIFICA SE O ALUNO ESTÁ COM UMA MATRÍCULA NO STATUS INFORMADO NA TURMA (USADO P/ ENTREGA DE ATIVIDADE) ---
    boolean existsByAlunoIdAndTurmaIdAndStatus(UUID alunoId, UUID turmaId, StatusMatricula status);
}
