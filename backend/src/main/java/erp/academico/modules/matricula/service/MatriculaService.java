package erp.academico.modules.matricula.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.infra.security.UsuarioDetails;
import erp.academico.modules.aluno.model.Aluno;
import erp.academico.modules.aluno.repository.AlunoRepository;
import erp.academico.modules.matricula.dto.AlterarStatusMatriculaRequestDTO;
import erp.academico.modules.matricula.dto.MatriculaRequestDTO;
import erp.academico.modules.matricula.dto.MatriculaResponseDTO;
import erp.academico.modules.matricula.model.Matricula;
import erp.academico.modules.matricula.model.StatusMatricula;
import erp.academico.modules.matricula.repository.MatriculaRepository;
import erp.academico.modules.turma.model.Turma;
import erp.academico.modules.turma.repository.TurmaRepository;
import erp.academico.modules.usuario.model.Usuario;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;

    // --- LISTA MATRÍCULAS ---
    @Transactional(readOnly = true)
    public Page<MatriculaResponseDTO> listar(UUID alunoId, Pageable pageable) {
        Page<Matricula> page = (alunoId == null)
                ? matriculaRepository.findAll(pageable)
                : matriculaRepository.findByAlunoId(alunoId, pageable);
        return page.map(this::toResponse);
    }

    // --- BUSCA MATRÍCULA POR ID ---
    @Transactional(readOnly = true)
    public MatriculaResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    // --- EFETIVA A MATRÍCULA DE UM ALUNO EM UMA TURMA ---
    @Transactional
    public MatriculaResponseDTO matricular(MatriculaRequestDTO dto) {
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", dto.getAlunoId()));

        Turma turma = turmaRepository.findById(dto.getTurmaId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma", dto.getTurmaId()));

        if (!Boolean.TRUE.equals(turma.getAtiva())) {
            throw new BusinessException("Não é possível matricular alunos em uma turma inativa.");
        }

        if (matriculaRepository.existsByAlunoIdAndTurmaPeriodoLetivoAndStatus(
                aluno.getId(), turma.getPeriodoLetivo(), StatusMatricula.ATIVA)) {
            throw new BusinessException(
                    "O aluno já possui matrícula ATIVA no período letivo " + turma.getPeriodoLetivo() + ".");
        }

        long ocupadas = matriculaRepository.countByTurmaIdAndStatus(turma.getId(), StatusMatricula.ATIVA);
        if (ocupadas >= turma.getCapacidadeMaxima()) {
            throw new BusinessException("A turma atingiu a capacidade máxima de "
                    + turma.getCapacidadeMaxima() + " alunos.");
        }

        // --- CRIA A MATRÍCULA ---
        Matricula matricula = Matricula.builder()
                .aluno(aluno)
                .turma(turma)
                .dataMatricula(dto.getDataMatricula() != null ? dto.getDataMatricula() : LocalDate.now())
                .status(StatusMatricula.ATIVA)
                .observacoes(dto.getObservacoes())
                .criadaPor(usuarioAutenticado())
                .build();

        matricula = matriculaRepository.save(matricula);

        aluno.setTurmaAtualId(turma.getId());
        alunoRepository.save(aluno);

        return toResponse(matricula);
    }

    // --- TRANCA UMA MATRÍCULA ATIVA ---
    @Transactional
    public MatriculaResponseDTO trancar(UUID id, AlterarStatusMatriculaRequestDTO dto) {
        return alterarStatus(id, StatusMatricula.TRANCADA, dto, true);
    }

    // --- CANCELA UMA MATRÍCULA ---
    @Transactional
    public MatriculaResponseDTO cancelar(UUID id, AlterarStatusMatriculaRequestDTO dto) {
        return alterarStatus(id, StatusMatricula.CANCELADA, dto, true);
    }

    // --- HELPERS ---
    private MatriculaResponseDTO alterarStatus(UUID id,
                                               StatusMatricula novoStatus,
                                               AlterarStatusMatriculaRequestDTO dto,
                                               boolean liberarTurmaAtualDoAluno) {
        Matricula matricula = buscarEntidade(id);

        if (matricula.getStatus() != StatusMatricula.ATIVA) {
            throw new BusinessException("Só é possível alterar o status de uma matrícula que esteja ATIVA. "
                    + "Status atual: " + matricula.getStatus() + ".");
        }

        matricula.setStatus(novoStatus);
        if (dto != null && dto.getObservacoes() != null) {
            matricula.setObservacoes(dto.getObservacoes());
        }

        if (liberarTurmaAtualDoAluno) {
            Aluno aluno = matricula.getAluno();
            if (aluno.getTurmaAtualId() != null && aluno.getTurmaAtualId().equals(matricula.getTurma().getId())) {
                aluno.setTurmaAtualId(null);
                alunoRepository.save(aluno);
            }
        }

        return toResponse(matriculaRepository.save(matricula));
    }

    private Matricula buscarEntidade(UUID id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula", id));
    }

    // --- RECUPERA O USUÁRIO AUTENTICADO A PARTIR DO SECURITY CONTEXT ---
    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UsuarioDetails details) {
            return details.getUsuario();
        }
        return null;
    }

    private MatriculaResponseDTO toResponse(Matricula m) {
        Usuario criador = m.getCriadaPor();
        return MatriculaResponseDTO.builder()
                .id(m.getId())
                .alunoId(m.getAluno().getId())
                .alunoNome(m.getAluno().getUsuario().getNome())
                .alunoMatriculaRA(m.getAluno().getMatriculaRA())
                .turmaId(m.getTurma().getId())
                .turmaCodigo(m.getTurma().getCodigo())
                .turmaPeriodoLetivo(m.getTurma().getPeriodoLetivo())
                .dataMatricula(m.getDataMatricula())
                .status(m.getStatus())
                .observacoes(m.getObservacoes())
                .criadaPorId(criador != null ? criador.getId() : null)
                .criadaPorNome(criador != null ? criador.getNome() : null)
                .criadoEm(m.getCriadoEm())
                .atualizadoEm(m.getAtualizadoEm())
                .build();
    }
}

