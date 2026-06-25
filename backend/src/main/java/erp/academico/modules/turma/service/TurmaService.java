package erp.academico.modules.turma.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.modules.aluno.dto.AlunoResponseDTO;
import erp.academico.modules.aluno.model.Aluno;
import erp.academico.modules.aluno.repository.AlunoRepository;
import erp.academico.modules.curso.model.Curso;
import erp.academico.modules.curso.service.CursoService;
import erp.academico.modules.disciplina.model.Disciplina;
import erp.academico.modules.disciplina.repository.DisciplinaRepository;
import erp.academico.modules.professor.model.Professor;
import erp.academico.modules.professor.repository.ProfessorDisciplinaRepository;
import erp.academico.modules.professor.repository.ProfessorRepository;
import erp.academico.modules.turma.dto.TurmaDisciplinaResponseDTO;
import erp.academico.modules.turma.dto.TurmaRequestDTO;
import erp.academico.modules.turma.dto.TurmaResponseDTO;
import erp.academico.modules.turma.dto.VincularDisciplinaTurmaRequestDTO;
import erp.academico.modules.turma.model.Turma;
import erp.academico.modules.turma.model.TurmaDisciplina;
import erp.academico.modules.turma.repository.TurmaDisciplinaRepository;
import erp.academico.modules.turma.repository.TurmaRepository;
import erp.academico.modules.usuario.dto.UsuarioResponseDTO;
import erp.academico.modules.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final CursoService cursoService;
    private final ProfessorRepository professorRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final ProfessorDisciplinaRepository professorDisciplinaRepository;
    private final AlunoRepository alunoRepository;

    @Transactional(readOnly = true)
    public Page<TurmaResponseDTO> listar(UUID cursoId, String periodoLetivo, Pageable pageable) {
        Page<Turma> page;
        if (cursoId != null) {
            page = turmaRepository.findByCursoId(cursoId, pageable);
        } else if (periodoLetivo != null && !periodoLetivo.isBlank()) {
            page = turmaRepository.findByPeriodoLetivo(periodoLetivo, pageable);
        } else {
            page = turmaRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    // --- BUSCA TURMA POR ID ---
    @Transactional(readOnly = true)
    public TurmaResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    // --- BUSCA TURMA PELO CÓDIGO ÚNICO ---
    @Transactional(readOnly = true)
    public TurmaResponseDTO buscarPorCodigo(String codigo) {
        Turma turma = turmaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Turma (código)", codigo));
        return toResponse(turma);
    }

    // --- CRIA UMA NOVA TURMA ---
    @Transactional
    public TurmaResponseDTO criar(TurmaRequestDTO dto) {
        if (turmaRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Já existe uma turma com o código: " + dto.getCodigo());
        }

        // --- BUSCA CURSO ---
        Curso curso = cursoService.buscarEntidade(dto.getCursoId());

        // --- BUSCA PROFESSOR REGENTE SE INFORMADO ---
        Professor regente = null;
        if (dto.getProfessorRegenteId() != null) {
            regente = buscarProfessor(dto.getProfessorRegenteId());
        }

        Turma turma = Turma.builder()
                .codigo(dto.getCodigo())
                .curso(curso)
                .periodoLetivo(dto.getPeriodoLetivo())
                .serie(dto.getSerie())
                .sala(dto.getSala())
                .turno(dto.getTurno())
                .professorRegente(regente)
                .capacidadeMaxima(dto.getCapacidadeMaxima())
                .ativa(dto.getAtiva() == null ? Boolean.TRUE : dto.getAtiva())
                .build();

        return toResponse(turmaRepository.save(turma));
    }

    // --- ATUALIZA OS DADOS DE UMA TURMA EXISTENTE ---
    @Transactional
    public TurmaResponseDTO atualizar(UUID id, TurmaRequestDTO dto) {
        Turma turma = buscarEntidade(id);

        if (!turma.getCodigo().equalsIgnoreCase(dto.getCodigo()) && turmaRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Já existe uma turma com o código: " + dto.getCodigo());
        }

        if (!turma.getCurso().getId().equals(dto.getCursoId())) {
            turma.setCurso(cursoService.buscarEntidade(dto.getCursoId()));
        }

        if (dto.getProfessorRegenteId() == null) {
            turma.setProfessorRegente(null);
        } else if (turma.getProfessorRegente() == null
                || !turma.getProfessorRegente().getId().equals(dto.getProfessorRegenteId())) {
            turma.setProfessorRegente(buscarProfessor(dto.getProfessorRegenteId()));
        }

        turma.setCodigo(dto.getCodigo());
        turma.setPeriodoLetivo(dto.getPeriodoLetivo());
        turma.setSerie(dto.getSerie());
        turma.setSala(dto.getSala());
        turma.setTurno(dto.getTurno());
        turma.setCapacidadeMaxima(dto.getCapacidadeMaxima());

        if (dto.getAtiva() != null) {
            turma.setAtiva(dto.getAtiva());
        }

        return toResponse(turmaRepository.save(turma));
    }

    // --- REMOVE UMA TURMA ---
    @Transactional
    public void deletar(UUID id) {
        Turma turma = buscarEntidade(id);
        turmaRepository.delete(turma);
    }

    // --- LISTA OS ALUNOS MATRICULADOS NA TURMA ---
    @Transactional(readOnly = true)
    public Page<AlunoResponseDTO> listarAlunos(UUID turmaId, Pageable pageable) {
        buscarEntidade(turmaId);
        return alunoRepository.findByTurmaAtualId(turmaId, pageable).map(this::toAlunoResponse);
    }

    // --- LISTA AS DISCIPLINAS VINCULADAS À TURMA ---
    @Transactional(readOnly = true)
    public List<TurmaDisciplinaResponseDTO> listarDisciplinas(UUID turmaId) {
        buscarEntidade(turmaId);
        return turmaDisciplinaRepository.findByTurmaId(turmaId)
                .stream()
                .map(this::toVinculoResponse)
                .toList();
    }

    // --- ADICIONA UMA DISCIPLINA À TURMA COM PROFESSOR RESPONSÁVEL E HORÁRIO ---
    @Transactional
    public TurmaDisciplinaResponseDTO vincularDisciplina(UUID turmaId, VincularDisciplinaTurmaRequestDTO dto) {
        Turma turma = buscarEntidade(turmaId);

        // --- VALIDA HORÁRIO ---
        if (!dto.getHorarioFim().isAfter(dto.getHorarioInicio())) {
            throw new BusinessException("O horário de término deve ser maior que o horário de início.");
        }

        // --- NÃO PERMITE VINCULAR A MESMA DISCIPLINA DUAS VEZES NA MESMA TURMA ---
        if (turmaDisciplinaRepository.existsByTurmaIdAndDisciplinaId(turmaId, dto.getDisciplinaId())) {
            throw new BusinessException("Esta disciplina já está vinculada à turma.");
        }

        Disciplina disciplina = disciplinaRepository.findById(dto.getDisciplinaId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina", dto.getDisciplinaId()));

        Professor professor = buscarProfessor(dto.getProfessorId());

        // --- O PROFESSOR PRECISA LECIONAR ESSA DISCIPLINA ---
        if (!professorDisciplinaRepository.existsByProfessorIdAndDisciplinaId(professor.getId(), disciplina.getId())) {
            throw new BusinessException(
                    "O professor selecionado não está habilitado a lecionar a disciplina informada.");
        }

        TurmaDisciplina vinculo = TurmaDisciplina.builder()
                .turma(turma)
                .disciplina(disciplina)
                .professor(professor)
                .diaSemana(dto.getDiaSemana())
                .horarioInicio(dto.getHorarioInicio())
                .horarioFim(dto.getHorarioFim())
                .build();

        return toVinculoResponse(turmaDisciplinaRepository.save(vinculo));
    }

    // --- REMOVE O VÍNCULO DE UMA DISCIPLINA NA TURMA ---
    @Transactional
    public void desvincularDisciplina(UUID turmaId, UUID disciplinaId) {
        TurmaDisciplina vinculo = turmaDisciplinaRepository
                .findByTurmaIdAndDisciplinaId(turmaId, disciplinaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vínculo turma/disciplina", turmaId + "/" + disciplinaId));
        turmaDisciplinaRepository.delete(vinculo);
    }

    // --- HELPERS ---

    private Turma buscarEntidade(UUID id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma", id));
    }

    private Professor buscarProfessor(UUID id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", id));
    }

    // --- CONVERSÕES PARA DTO ---

    private TurmaResponseDTO toResponse(Turma t) {
        Professor regente = t.getProfessorRegente();
        return TurmaResponseDTO.builder()
                .id(t.getId())
                .codigo(t.getCodigo())
                .cursoId(t.getCurso().getId())
                .cursoNome(t.getCurso().getNome())
                .periodoLetivo(t.getPeriodoLetivo())
                .serie(t.getSerie())
                .sala(t.getSala())
                .turno(t.getTurno())
                .professorRegenteId(regente != null ? regente.getId() : null)
                .professorRegenteNome(regente != null ? regente.getUsuario().getNome() : null)
                .capacidadeMaxima(t.getCapacidadeMaxima())
                .ativa(t.getAtiva())
                .criadoEm(t.getCriadoEm())
                .atualizadoEm(t.getAtualizadoEm())
                .build();
    }

    private TurmaDisciplinaResponseDTO toVinculoResponse(TurmaDisciplina v) {
        return TurmaDisciplinaResponseDTO.builder()
                .id(v.getId())
                .turmaId(v.getTurma().getId())
                .disciplinaId(v.getDisciplina().getId())
                .disciplinaCodigo(v.getDisciplina().getCodigo())
                .disciplinaNome(v.getDisciplina().getNome())
                .professorId(v.getProfessor().getId())
                .professorNome(v.getProfessor().getUsuario().getNome())
                .diaSemana(v.getDiaSemana())
                .horarioInicio(v.getHorarioInicio())
                .horarioFim(v.getHorarioFim())
                .criadoEm(v.getCriadoEm())
                .build();
    }

    private AlunoResponseDTO toAlunoResponse(Aluno a) {
        Usuario u = a.getUsuario();
        UsuarioResponseDTO usuarioDto = UsuarioResponseDTO.builder()
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .cpf(u.getCpf())
                .telefone(u.getTelefone())
                .dataNascimento(u.getDataNascimento())
                .ativo(u.getAtivo())
                .role(u.getRole())
                .criadoEm(u.getCriadoEm())
                .atualizadoEm(u.getAtualizadoEm())
                .build();

        return AlunoResponseDTO.builder()
                .id(a.getId())
                .usuario(usuarioDto)
                .matriculaRA(a.getMatriculaRA())
                .dataIngresso(a.getDataIngresso())
                .status(a.getStatus())
                .turmaAtualId(a.getTurmaAtualId())
                .observacoes(a.getObservacoes())
                .criadoEm(a.getCriadoEm())
                .atualizadoEm(a.getAtualizadoEm())
                .build();
    }
}

