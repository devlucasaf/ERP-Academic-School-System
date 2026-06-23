package erp.academico.modules.disciplina.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.modules.curso.model.Curso;
import erp.academico.modules.curso.service.CursoService;
import erp.academico.modules.disciplina.dto.DisciplinaPrerequisitoResponseDTO;
import erp.academico.modules.disciplina.dto.DisciplinaRequestDTO;
import erp.academico.modules.disciplina.dto.DisciplinaResponseDTO;
import erp.academico.modules.disciplina.dto.VincularPrerequisitoRequestDTO;
import erp.academico.modules.disciplina.model.Disciplina;
import erp.academico.modules.disciplina.model.DisciplinaPrerequisito;
import erp.academico.modules.disciplina.repository.DisciplinaPrerequisitoRepository;
import erp.academico.modules.disciplina.repository.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaPrerequisitoRepository disciplinaPrerequisitoRepository;
    private final CursoService cursoService;

    @Transactional(readOnly = true)
    public Page<DisciplinaResponseDTO> listar(Pageable pageable) {
        return disciplinaRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public DisciplinaResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public DisciplinaResponseDTO buscarPorCodigo(String codigo) {
        Disciplina disciplina = disciplinaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina (código)", codigo));
        return toResponse(disciplina);
    }

    @Transactional(readOnly = true)
    public Page<DisciplinaResponseDTO> listarPorCurso(UUID cursoId, Pageable pageable) {
        cursoService.buscarEntidade(cursoId); // valida existência
        return disciplinaRepository.findByCursoId(cursoId, pageable).map(this::toResponse);
    }

    @Transactional
    public DisciplinaResponseDTO criar(DisciplinaRequestDTO dto) {
        if (disciplinaRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Já existe uma disciplina com o código: " + dto.getCodigo());
        }

        Curso curso = cursoService.buscarEntidade(dto.getCursoId());

        Disciplina disciplina = Disciplina.builder()
                .codigo(dto.getCodigo())
                .nome(dto.getNome())
                .ementa(dto.getEmenta())
                .cargaHoraria(dto.getCargaHoraria())
                .curso(curso)
                .periodo(dto.getPeriodo())
                .ativo(dto.getAtivo() == null ? Boolean.TRUE : dto.getAtivo())
                .build();

        return toResponse(disciplinaRepository.save(disciplina));
    }

    @Transactional
    public DisciplinaResponseDTO atualizar(UUID id, DisciplinaRequestDTO dto) {
        Disciplina disciplina = buscarEntidade(id);

        if (!disciplina.getCodigo().equalsIgnoreCase(dto.getCodigo())
                && disciplinaRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Já existe uma disciplina com o código: " + dto.getCodigo());
        }

        if (!disciplina.getCurso().getId().equals(dto.getCursoId())) {
            disciplina.setCurso(cursoService.buscarEntidade(dto.getCursoId()));
        }

        disciplina.setCodigo(dto.getCodigo());
        disciplina.setNome(dto.getNome());
        disciplina.setEmenta(dto.getEmenta());
        disciplina.setCargaHoraria(dto.getCargaHoraria());
        disciplina.setPeriodo(dto.getPeriodo());
        if (dto.getAtivo() != null) {
            disciplina.setAtivo(dto.getAtivo());
        }

        return toResponse(disciplinaRepository.save(disciplina));
    }

    @Transactional
    public void deletar(UUID id) {
        Disciplina disciplina = buscarEntidade(id);
        disciplinaRepository.delete(disciplina);
    }

    // --- Pré-requisitos ---

    @Transactional(readOnly = true)
    public List<DisciplinaPrerequisitoResponseDTO> listarPrerequisitos(UUID disciplinaId) {
        buscarEntidade(disciplinaId);
        return disciplinaPrerequisitoRepository.findByDisciplinaId(disciplinaId)
                .stream()
                .map(this::toPrerequisitoResponse)
                .toList();
    }

    @Transactional
    public DisciplinaPrerequisitoResponseDTO vincularPrerequisito(UUID disciplinaId,
                                                                  VincularPrerequisitoRequestDTO dto) {
        if (disciplinaId.equals(dto.getPrerequisitoId())) {
            throw new BusinessException("Uma disciplina não pode ser pré-requisito de si mesma.");
        }

        Disciplina disciplina = buscarEntidade(disciplinaId);
        Disciplina prerequisito = buscarEntidade(dto.getPrerequisitoId());

        if (disciplinaPrerequisitoRepository.existsByDisciplinaIdAndPrerequisitoId(
                disciplinaId, prerequisito.getId())) {
            throw new BusinessException("Este pré-requisito já está vinculado à disciplina.");
        }

        DisciplinaPrerequisito vinculo = DisciplinaPrerequisito.builder()
                .disciplina(disciplina)
                .prerequisito(prerequisito)
                .build();

        return toPrerequisitoResponse(disciplinaPrerequisitoRepository.save(vinculo));
    }

    @Transactional
    public void desvincularPrerequisito(UUID disciplinaId, UUID prerequisitoId) {
        DisciplinaPrerequisito vinculo = disciplinaPrerequisitoRepository
                .findByDisciplinaIdAndPrerequisitoId(disciplinaId, prerequisitoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pré-requisito de disciplina", disciplinaId + "/" + prerequisitoId));
        disciplinaPrerequisitoRepository.delete(vinculo);
    }

    // --- Helpers ---

    /**
     * Acesso público para outros services que precisam da entidade {@link Disciplina}.
     */
    @Transactional(readOnly = true)
    public Disciplina buscarEntidade(UUID id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina", id));
    }

    private DisciplinaResponseDTO toResponse(Disciplina d) {
        return DisciplinaResponseDTO.builder()
                .id(d.getId())
                .codigo(d.getCodigo())
                .nome(d.getNome())
                .ementa(d.getEmenta())
                .cargaHoraria(d.getCargaHoraria())
                .cursoId(d.getCurso().getId())
                .cursoNome(d.getCurso().getNome())
                .periodo(d.getPeriodo())
                .ativo(d.getAtivo())
                .criadoEm(d.getCriadoEm())
                .atualizadoEm(d.getAtualizadoEm())
                .build();
    }

    private DisciplinaPrerequisitoResponseDTO toPrerequisitoResponse(DisciplinaPrerequisito v) {
        Disciplina pre = v.getPrerequisito();
        return DisciplinaPrerequisitoResponseDTO.builder()
                .id(v.getId())
                .disciplinaId(v.getDisciplina().getId())
                .prerequisitoId(pre.getId())
                .prerequisitoCodigo(pre.getCodigo())
                .prerequisitoNome(pre.getNome())
                .criadoEm(v.getCriadoEm())
                .build();
    }
}

