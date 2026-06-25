package erp.academico.modules.nota.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.infra.security.UsuarioDetails;
import erp.academico.modules.aluno.model.Aluno;
import erp.academico.modules.aluno.repository.AlunoRepository;
import erp.academico.modules.nota.dto.BoletimResponseDTO;
import erp.academico.modules.nota.dto.DisciplinaBoletim;
import erp.academico.modules.nota.dto.NotaRequestDTO;
import erp.academico.modules.nota.dto.NotaResponseDTO;
import erp.academico.modules.nota.model.Nota;
import erp.academico.modules.nota.repository.NotaRepository;
import erp.academico.modules.turma.model.TurmaDisciplina;
import erp.academico.modules.turma.repository.TurmaDisciplinaRepository;
import erp.academico.modules.usuario.model.RoleUsuario;
import erp.academico.modules.usuario.model.Usuario;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotaService {

    private static final BigDecimal NOTA_MIN = new BigDecimal("0.00");
    private static final BigDecimal NOTA_MAX = new BigDecimal("10.00");
    private static final BigDecimal PESO_DEFAULT = BigDecimal.ONE;

    private final NotaRepository notaRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;

    // --- LANÇA UMA NOVA NOTA ---
    @Transactional
    public NotaResponseDTO lancar(NotaRequestDTO dto) {
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", dto.getAlunoId()));

        TurmaDisciplina td = turmaDisciplinaRepository.findById(dto.getTurmaDisciplinaId())
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo turma/disciplina", dto.getTurmaDisciplinaId()));

        validarValor(dto.getValor());

        Usuario autenticado = usuarioAutenticadoObrigatorio();
        validarPermissaoLancamento(autenticado, td);

        Nota nota = Nota.builder()
                .aluno(aluno)
                .turmaDisciplina(td)
                .periodoAvaliacao(dto.getPeriodoAvaliacao())
                .tipoAvaliacao(dto.getTipoAvaliacao())
                .valor(dto.getValor())
                .peso(dto.getPeso() != null ? dto.getPeso() : PESO_DEFAULT)
                .observacoes(dto.getObservacoes())
                .lancadaPor(autenticado)
                .build();

        return toResponse(notaRepository.save(nota));
    }

    // --- ATUALIZA UMA NOTA EXISTENTE ---
    @Transactional
    public NotaResponseDTO atualizar(UUID id, NotaRequestDTO dto) {
        Nota nota = buscarEntidade(id);

        validarValor(dto.getValor());

        Usuario autenticado = usuarioAutenticadoObrigatorio();
        validarPermissaoLancamento(autenticado, nota.getTurmaDisciplina());

        nota.setPeriodoAvaliacao(dto.getPeriodoAvaliacao());
        nota.setTipoAvaliacao(dto.getTipoAvaliacao());
        nota.setValor(dto.getValor());

        if (dto.getPeso() != null) {
            nota.setPeso(dto.getPeso());
        }
        nota.setObservacoes(dto.getObservacoes());
        nota.setLancadaPor(autenticado);

        return toResponse(notaRepository.save(nota));
    }

    // --- REMOVE UMA NOTA ---
    @Transactional
    public void deletar(UUID id) {
        Nota nota = buscarEntidade(id);

        Usuario autenticado = usuarioAutenticadoObrigatorio();
        validarPermissaoLancamento(autenticado, nota.getTurmaDisciplina());

        notaRepository.delete(nota);
    }

    @Transactional(readOnly = true)
    public List<NotaResponseDTO> listarPorTurmaDisciplina(UUID turmaId, UUID disciplinaId) {
        return notaRepository
                .findByTurmaDisciplina_TurmaIdAndTurmaDisciplina_DisciplinaId(turmaId, disciplinaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // --- LISTA TODAS AS NOTAS DE UM ALUNO ---
    @Transactional(readOnly = true)
    public List<NotaResponseDTO> listarPorAluno(UUID alunoId) {
        return notaRepository.findByAlunoId(alunoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // --- BOLETIM CONSOLIDADO DO ALUNO PARA UM PERÍODO LETIVO ---
    @Transactional(readOnly = true)
    public BoletimResponseDTO boletim(UUID alunoId, String periodoLetivo) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", alunoId));

        List<Nota> notas = notaRepository
                .findByAlunoIdAndTurmaDisciplina_TurmaPeriodoLetivo(alunoId, periodoLetivo);

        // --- AGRUPA NOTAS POR DISCIPLINA ---
        Map<UUID, List<Nota>> porDisciplina = new LinkedHashMap<>();
        for (Nota n : notas) {
            porDisciplina
                    .computeIfAbsent(n.getTurmaDisciplina().getDisciplina().getId(), k -> new ArrayList<>())
                    .add(n);
        }

        // --- CONSTRÓI CADA LINHA DO BOLETIM CALCULANDO A MÉDIA PONDERADA ---
        List<DisciplinaBoletim> linhas = porDisciplina.values().stream()
                .map(grupo -> {
                    var primeira = grupo.get(0).getTurmaDisciplina().getDisciplina();
                    return DisciplinaBoletim.builder()
                            .disciplinaId(primeira.getId())
                            .disciplinaCodigo(primeira.getCodigo())
                            .disciplinaNome(primeira.getNome())
                            .mediaFinal(calcularMediaPonderada(grupo))
                            .notas(grupo.stream()
                                    .sorted(Comparator.comparing(Nota::getPeriodoAvaliacao))
                                    .map(this::toResponse)
                                    .toList())
                            .build();
                })
                .toList();

        return BoletimResponseDTO.builder()
                .alunoId(aluno.getId())
                .alunoNome(aluno.getUsuario().getNome())
                .alunoMatriculaRA(aluno.getMatriculaRA())
                .periodoLetivo(periodoLetivo)
                .disciplinas(linhas)
                .build();
    }

    // --- VALIDA QUE A NOTA ESTÁ DENTRO DO INTERVALO PERMITIDO ---
    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(NOTA_MIN) < 0 || valor.compareTo(NOTA_MAX) > 0) {
            throw new BusinessException("A nota deve estar entre " + NOTA_MIN + " e " + NOTA_MAX + ".");
        }
    }

    // --- VALIDA QUE O USUÁRIO AUTENTICADO PODE LANÇAR/EDITAR NOTAS PARA AQUELA TURMA/DISCIPLINA ---
    private void validarPermissaoLancamento(Usuario autenticado, TurmaDisciplina td) {
        RoleUsuario role = autenticado.getRole();

        if (role == RoleUsuario.ADMIN || role == RoleUsuario.COORDENADOR) {
            return;
        }

        if (role == RoleUsuario.PROFESSOR) {
            UUID profUsuarioId = td.getProfessor().getUsuario().getId();
            if (profUsuarioId.equals(autenticado.getId())) {
                return;
            }
            throw new BusinessException("Apenas o professor responsável pela disciplina pode lançar notas para esta turma.");
        }

        throw new BusinessException("Você não tem permissão para lançar notas.");
    }

    // --- CALCULA A MÉDIA PONDERADA DE UM CONJUNTO DE NOTAS ---
    private BigDecimal calcularMediaPonderada(List<Nota> notas) {
        BigDecimal somaPonderada = BigDecimal.ZERO;
        BigDecimal somaPesos = BigDecimal.ZERO;
        for (Nota n : notas) {
            somaPonderada = somaPonderada.add(n.getValor().multiply(n.getPeso()));
            somaPesos = somaPesos.add(n.getPeso());
        }

        if (somaPesos.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return somaPonderada.divide(somaPesos, 2, RoundingMode.HALF_UP);
    }

    // --- BUSCA A ENTIDADE ---
    private Nota buscarEntidade(UUID id) {
        return notaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota", id));
    }

    // --- RECUPERA O USUÁRIO AUTENTICADO ---
    private Usuario usuarioAutenticadoObrigatorio() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UsuarioDetails details)) {
            throw new BusinessException("Não foi possível identificar o usuário autenticado.");
        }
        return details.getUsuario();
    }

    // --- CONVERSÃO ENTIDADE ---
    private NotaResponseDTO toResponse(Nota n) {
        TurmaDisciplina td = n.getTurmaDisciplina();
        Usuario lancador = n.getLancadaPor();
        return NotaResponseDTO.builder()
                .id(n.getId())
                .alunoId(n.getAluno().getId())
                .alunoNome(n.getAluno().getUsuario().getNome())
                .alunoMatriculaRA(n.getAluno().getMatriculaRA())
                .turmaDisciplinaId(td.getId())
                .turmaId(td.getTurma().getId())
                .turmaCodigo(td.getTurma().getCodigo())
                .disciplinaId(td.getDisciplina().getId())
                .disciplinaNome(td.getDisciplina().getNome())
                .professorId(td.getProfessor().getId())
                .professorNome(td.getProfessor().getUsuario().getNome())
                .periodoAvaliacao(n.getPeriodoAvaliacao())
                .tipoAvaliacao(n.getTipoAvaliacao())
                .valor(n.getValor())
                .peso(n.getPeso())
                .observacoes(n.getObservacoes())
                .lancadaPorId(lancador != null ? lancador.getId() : null)
                .lancadaPorNome(lancador != null ? lancador.getNome() : null)
                .lancadaEm(n.getLancadaEm())
                .atualizadaEm(n.getAtualizadaEm())
                .build();
    }
}

