package erp.academico.modules.atividade.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.infra.security.UsuarioDetails;
import erp.academico.infra.storage.StorageService;
import erp.academico.modules.aluno.model.Aluno;
import erp.academico.modules.aluno.repository.AlunoRepository;
import erp.academico.modules.atividade.dto.AtividadeRequestDTO;
import erp.academico.modules.atividade.dto.AtividadeResponseDTO;
import erp.academico.modules.atividade.dto.AvaliarEntregaRequestDTO;
import erp.academico.modules.atividade.dto.EntregaAtividadeResponseDTO;
import erp.academico.modules.atividade.model.Atividade;
import erp.academico.modules.atividade.model.EntregaAtividade;
import erp.academico.modules.atividade.model.StatusEntrega;
import erp.academico.modules.atividade.repository.AtividadeRepository;
import erp.academico.modules.atividade.repository.EntregaAtividadeRepository;
import erp.academico.modules.matricula.model.StatusMatricula;
import erp.academico.modules.matricula.repository.MatriculaRepository;
import erp.academico.modules.professor.model.Professor;
import erp.academico.modules.turma.model.TurmaDisciplina;
import erp.academico.modules.turma.repository.TurmaDisciplinaRepository;
import erp.academico.modules.usuario.model.RoleUsuario;
import erp.academico.modules.usuario.model.Usuario;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtividadeService {

    private static final String SUBDIR_ENTREGAS = "atividades/entregas";

    private final AtividadeRepository atividadeRepository;
    private final EntregaAtividadeRepository entregaRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;
    private final StorageService storageService;

    // --- LISTA AS ATIVIDADES DE UMA TURMA/DISCIPLINA ---
    @Transactional(readOnly = true)
    public Page<AtividadeResponseDTO> listarPorTurmaDisciplina(UUID turmaDisciplinaId, Pageable pageable) {
        buscarTurmaDisciplina(turmaDisciplinaId);
        return atividadeRepository.findByTurmaDisciplinaId(turmaDisciplinaId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AtividadeResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    // --- CRIAÇÃO DE UMA NOVA ATIVIDADE ---
    @Transactional
    public AtividadeResponseDTO criar(AtividadeRequestDTO dto) {
        TurmaDisciplina td = buscarTurmaDisciplina(dto.getTurmaDisciplinaId());
        Usuario autenticado = usuarioAutenticadoOuFalha();
        Professor professor = validarProfessorDaDisciplina(td, autenticado);

        Atividade atividade = Atividade.builder()
                .turmaDisciplina(td)
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .tipo(dto.getTipo())
                .dataPostagem(LocalDateTime.now())
                .dataEntrega(dto.getDataEntrega())
                .valorMaximo(dto.getValorMaximo())
                .professor(professor)
                .ativa(dto.getAtiva() == null ? Boolean.TRUE : dto.getAtiva())
                .build();

        if (atividade.getDataEntrega().isBefore(atividade.getDataPostagem())) {
            throw new BusinessException("A data de entrega não pode ser anterior à data de postagem.");
        }

        return toResponse(atividadeRepository.save(atividade));
    }

    // --- ATUALIZAÇÃO DE METADADOS DA ATIVIDADE ---
    @Transactional
    public AtividadeResponseDTO atualizar(UUID id, AtividadeRequestDTO dto) {
        Atividade atividade = buscarEntidade(id);
        Usuario autenticado = usuarioAutenticadoOuFalha();
        validarPodeEditarAtividade(atividade, autenticado);

        if (!atividade.getTurmaDisciplina().getId().equals(dto.getTurmaDisciplinaId())) {
            atividade.setTurmaDisciplina(buscarTurmaDisciplina(dto.getTurmaDisciplinaId()));
        }

        atividade.setTitulo(dto.getTitulo());
        atividade.setDescricao(dto.getDescricao());
        atividade.setTipo(dto.getTipo());
        atividade.setDataEntrega(dto.getDataEntrega());
        atividade.setValorMaximo(dto.getValorMaximo());

        if (dto.getAtiva() != null) {
            atividade.setAtiva(dto.getAtiva());
        }

        if (atividade.getDataEntrega().isBefore(atividade.getDataPostagem())) {
            throw new BusinessException("A data de entrega não pode ser anterior à data de postagem.");
        }

        return toResponse(atividadeRepository.save(atividade));
    }

    @Transactional
    public void deletar(UUID id) {
        Atividade atividade = buscarEntidade(id);
        Usuario autenticado = usuarioAutenticadoOuFalha();
        validarPodeEditarAtividade(atividade, autenticado);

        entregaRepository.findByAtividadeId(id)
                .forEach(e -> storageService.delete(e.getArquivoUrl()));
        atividadeRepository.delete(atividade);
    }

    // --- ALUNO ENTREGA A ATIVIDADE ---
    @Transactional
    public EntregaAtividadeResponseDTO entregar(UUID atividadeId, MultipartFile arquivo, String comentarioAluno) {
        Atividade atividade = buscarEntidade(atividadeId);

        if (Boolean.FALSE.equals(atividade.getAtiva())) {
            throw new BusinessException("Esta atividade não está mais ativa.");
        }

        Usuario autenticado = usuarioAutenticadoOuFalha();
        Aluno aluno = alunoRepository.findByUsuarioId(autenticado.getId())
                .orElseThrow(() -> new BusinessException("O usuário autenticado não está vinculado a um aluno."));

        UUID turmaId = atividade.getTurmaDisciplina().getTurma().getId();
        if (!matriculaRepository.existsByAlunoIdAndTurmaIdAndStatus(aluno.getId(), turmaId, StatusMatricula.ATIVA)) {
            throw new BusinessException("Aluno não possui matrícula ATIVA na turma desta atividade.");
        }

        LocalDateTime agora = LocalDateTime.now();
        StatusEntrega status = agora.isAfter(atividade.getDataEntrega()) ? StatusEntrega.ATRASADA : StatusEntrega.ENTREGUE;

        EntregaAtividade entrega = entregaRepository.findByAtividadeIdAndAlunoId(atividadeId, aluno.getId())
                .orElseGet(() -> EntregaAtividade.builder()
                        .atividade(atividade)
                        .aluno(aluno)
                        .status(StatusEntrega.PENDENTE)
                        .build());

        if (entrega.getStatus() == StatusEntrega.AVALIADA) {
            throw new BusinessException("Esta entrega já foi avaliada e não pode mais ser modificada.");
        }

        if (entrega.getArquivoUrl() != null) {
            storageService.delete(entrega.getArquivoUrl());
        }

        String url = storageService.store(arquivo, SUBDIR_ENTREGAS);
        entrega.setArquivoUrl(url);
        entrega.setComentarioAluno(comentarioAluno);
        entrega.setDataEntrega(agora);
        entrega.setStatus(status);

        return toEntregaResponse(entregaRepository.save(entrega));
    }

    // --- PROFESSOR AVALIA UMA ENTREGA ---
    @Transactional
    public EntregaAtividadeResponseDTO avaliar(UUID entregaId, AvaliarEntregaRequestDTO dto) {
        EntregaAtividade entrega = entregaRepository.findById(entregaId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega de atividade", entregaId));

        Usuario autenticado = usuarioAutenticadoOuFalha();
        validarPodeEditarAtividade(entrega.getAtividade(), autenticado);

        if (dto.getNota().doubleValue() > entrega.getAtividade().getValorMaximo().doubleValue()) {
            throw new BusinessException("Nota não pode ultrapassar o valor máximo da atividade ("
                    + entrega.getAtividade().getValorMaximo() + ").");
        }

        entrega.setNota(dto.getNota());
        entrega.setFeedback(dto.getFeedback());
        entrega.setStatus(StatusEntrega.AVALIADA);

        return toEntregaResponse(entregaRepository.save(entrega));
    }

    // --- LISTA AS ENTREGAS DE UM ALUNO ---
    @Transactional(readOnly = true)
    public Page<EntregaAtividadeResponseDTO> listarEntregasDoAluno(UUID alunoId, StatusEntrega status, Pageable pageable) {
        Page<EntregaAtividade> page = (status == null)
                ? entregaRepository.findByAlunoId(alunoId, pageable)
                : entregaRepository.findByAlunoIdAndStatus(alunoId, status, pageable);
        return page.map(this::toEntregaResponse);
    }

    // --- LISTA AS ENTREGAS DE UMA ATIVIDADE ---
    @Transactional(readOnly = true)
    public List<EntregaAtividadeResponseDTO> listarEntregasDaAtividade(UUID atividadeId) {
        buscarEntidade(atividadeId);
        return entregaRepository.findByAtividadeId(atividadeId)
                .stream()
                .map(this::toEntregaResponse)
                .toList();
    }

    private Atividade buscarEntidade(UUID id) {
        return atividadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade", id));
    }

    private TurmaDisciplina buscarTurmaDisciplina(UUID id) {
        return turmaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo turma/disciplina", id));
    }

    // --- RECUPERA O Usuario AUTENTICADO A PARTIR DO SECURITYCONTEXT ---
    private Usuario usuarioAutenticadoOuFalha() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioDetails ud)) {
            throw new BusinessException("Usuário autenticado não identificado.");
        }
        return ud.getUsuario();
    }

    // --- VALIDA QUE O PROFESSOR LOGADO É O RESPONSÁVEL PELA DISCIPLINA ALOCADA NA TURMA ---
    private Professor validarProfessorDaDisciplina(TurmaDisciplina td, Usuario autenticado) {
        Professor professor = td.getProfessor();
        if (professor == null || professor.getUsuario() == null) {
            throw new BusinessException("Vínculo turma/disciplina não possui professor responsável.");
        }

        if (autenticado.getRole() == RoleUsuario.ADMIN
                || autenticado.getRole() == RoleUsuario.COORDENADOR) {
            return professor;
        }
        if (!professor.getUsuario().getId().equals(autenticado.getId())) {
            throw new BusinessException("Apenas o professor responsável pela disciplina pode realizar esta operação.");
        }
        return professor;
    }

    // --- VERIFICA SE O USUÁRIO LOGADO PODE EDITAR/AVALIAR A ATIVIDADE ---
    private void validarPodeEditarAtividade(Atividade atividade, Usuario autenticado) {
        if (autenticado.getRole() == RoleUsuario.ADMIN
                || autenticado.getRole() == RoleUsuario.COORDENADOR) {
            return;
        }

        if (!atividade.getProfessor().getUsuario().getId().equals(autenticado.getId())) {
            throw new BusinessException("Apenas o professor responsável pode editar/avaliar esta atividade.");
        }
    }

    // --- CONVERTE Atividade ---
    private AtividadeResponseDTO toResponse(Atividade a) {
        return AtividadeResponseDTO.builder()
                .id(a.getId())
                .turmaDisciplinaId(a.getTurmaDisciplina().getId())
                .titulo(a.getTitulo())
                .descricao(a.getDescricao())
                .tipo(a.getTipo())
                .dataPostagem(a.getDataPostagem())
                .dataEntrega(a.getDataEntrega())
                .valorMaximo(a.getValorMaximo())
                .professorId(a.getProfessor().getId())
                .professorNome(a.getProfessor().getUsuario() == null ? null : a.getProfessor().getUsuario().getNome())
                .ativa(a.getAtiva())
                .criadoEm(a.getCriadoEm())
                .atualizadoEm(a.getAtualizadoEm())
                .build();
    }

    // --- CONVERTE EntregaAtividade ---
    private EntregaAtividadeResponseDTO toEntregaResponse(EntregaAtividade e) {
        return EntregaAtividadeResponseDTO.builder()
                .id(e.getId())
                .atividadeId(e.getAtividade().getId())
                .atividadeTitulo(e.getAtividade().getTitulo())
                .alunoId(e.getAluno().getId())
                .alunoNome(e.getAluno().getUsuario() == null ? null : e.getAluno().getUsuario().getNome())
                .arquivoUrl(e.getArquivoUrl())
                .comentarioAluno(e.getComentarioAluno())
                .dataEntrega(e.getDataEntrega())
                .nota(e.getNota())
                .feedback(e.getFeedback())
                .status(e.getStatus())
                .criadoEm(e.getCriadoEm())
                .atualizadoEm(e.getAtualizadoEm())
                .build();
    }
}

