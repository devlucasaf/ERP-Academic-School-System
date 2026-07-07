package erp.academico.modules.biblioteca.emprestimo.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.infra.security.UsuarioDetails;
import erp.academico.modules.biblioteca.configuracao.model.ConfiguracaoBiblioteca;
import erp.academico.modules.biblioteca.configuracao.service.ConfiguracaoBibliotecaService;
import erp.academico.modules.biblioteca.emprestimo.dto.EmprestimoRequestDTO;
import erp.academico.modules.biblioteca.emprestimo.dto.EmprestimoResponseDTO;
import erp.academico.modules.biblioteca.emprestimo.model.Emprestimo;
import erp.academico.modules.biblioteca.emprestimo.model.StatusEmprestimo;
import erp.academico.modules.biblioteca.emprestimo.repository.EmprestimoRepository;
import erp.academico.modules.biblioteca.event.MultaGeradaEvent;
import erp.academico.modules.biblioteca.exemplar.model.Exemplar;
import erp.academico.modules.biblioteca.exemplar.model.StatusExemplar;
import erp.academico.modules.biblioteca.exemplar.repository.ExemplarRepository;
import erp.academico.modules.biblioteca.multa.model.Multa;
import erp.academico.modules.biblioteca.multa.model.StatusMulta;
import erp.academico.modules.biblioteca.multa.repository.MultaRepository;
import erp.academico.modules.biblioteca.reserva.model.Reserva;
import erp.academico.modules.biblioteca.reserva.model.StatusReserva;
import erp.academico.modules.biblioteca.reserva.repository.ReservaRepository;
import erp.academico.modules.usuario.model.RoleUsuario;
import erp.academico.modules.usuario.model.Usuario;
import erp.academico.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// --- ORQUESTRA EMPRÉSTIMO, DEVOLUÇÃO, RENOVAÇÃO E GERAÇÃO DE MULTAS ---
@Slf4j
@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final ExemplarRepository exemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final MultaRepository multaRepository;
    private final ReservaRepository reservaRepository;
    private final ConfiguracaoBibliotecaService configuracaoService;
    private final ApplicationEventPublisher eventPublisher;

    // --- REGISTRA UM NOVO EMPRÉSTIMO ---
    @Transactional
    public EmprestimoResponseDTO registrar(EmprestimoRequestDTO dto) {
        ConfiguracaoBiblioteca cfg = configuracaoService.obter();

        Exemplar exemplar = resolverExemplar(dto);
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", dto.getUsuarioId()));

        // --- REGRAS DE NEGÓCIO ---
        if (exemplar.getStatus() != StatusExemplar.DISPONIVEL) {
            throw new BusinessException("Exemplar não está disponível para empréstimo. Status atual: "
                    + exemplar.getStatus());
        }
        if (multaRepository.existePendenteDoUsuario(usuario.getId())) {
            throw new BusinessException("Usuário possui multa pendente e não pode realizar novos empréstimos.");
        }
        long ativos = emprestimoRepository.countByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.ATIVO)
                + emprestimoRepository.countByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.ATRASADO);
        if (ativos >= cfg.getMaxEmprestimosSimultaneos()) {
            throw new BusinessException("Limite de empréstimos simultâneos atingido ("
                    + cfg.getMaxEmprestimosSimultaneos() + ").");
        }

        LocalDateTime agora = LocalDateTime.now();
        int prazoDias = prazoDias(usuario, cfg);

        Emprestimo emp = Emprestimo.builder()
                .exemplar(exemplar)
                .usuario(usuario)
                .dataEmprestimo(agora)
                .dataDevolucaoPrevista(agora.plusDays(prazoDias))
                .renovacoes(0)
                .status(StatusEmprestimo.ATIVO)
                .criadoPor(usuarioAutenticadoOuFalha())
                .build();

        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        exemplarRepository.save(exemplar);

        // --- SE ESTE USUÁRIO ERA O PRIMEIRO DA FILA DE RESERVA, MARCAR COMO ATENDIDA ---
        reservaRepository.findFirstByLivroIdAndStatusOrderByPosicaoFilaAsc(
                        exemplar.getLivro().getId(), StatusReserva.AGUARDANDO)
                .filter(r -> r.getUsuario().getId().equals(usuario.getId()))
                .ifPresent(r -> {
                    r.setStatus(StatusReserva.ATENDIDA);
                    reservaRepository.save(r);
                    reordenarFila(exemplar.getLivro().getId());
                });

        return toResponse(emprestimoRepository.save(emp));
    }

    // --- REGISTRA A DEVOLUÇÃO E GERA MULTA SE ATRASADO ---
    @Transactional
    public EmprestimoResponseDTO devolver(UUID emprestimoId) {
        Emprestimo emp = buscarEntidade(emprestimoId);

        if (emp.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new BusinessException("Este empréstimo já foi devolvido.");
        }

        LocalDateTime agora = LocalDateTime.now();
        emp.setDataDevolucaoEfetiva(agora);
        emp.setStatus(StatusEmprestimo.DEVOLVIDO);

        int diasAtraso = calcularDiasAtraso(emp.getDataDevolucaoPrevista(), agora);
        if (diasAtraso > 0) {
            gerarMulta(emp, diasAtraso);
        }

        // --- ATUALIZA STATUS DO EXEMPLAR CONFORME FILA DE RESERVA ---
        Exemplar exemplar = emp.getExemplar();
        Optional<Reserva> primeiroDaFila = reservaRepository
                .findFirstByLivroIdAndStatusOrderByPosicaoFilaAsc(exemplar.getLivro().getId(), StatusReserva.AGUARDANDO);

        if (primeiroDaFila.isPresent()) {
            exemplar.setStatus(StatusExemplar.RESERVADO);
            // --- NOTIFICAR (log por ora; integração com módulo de comunicação futura) ---
            log.info("Notificando usuário {} que o livro '{}' está disponível para retirada.",
                    primeiroDaFila.get().getUsuario().getEmail(),
                    exemplar.getLivro().getTitulo());
        } else {
            exemplar.setStatus(StatusExemplar.DISPONIVEL);
        }
        exemplarRepository.save(exemplar);

        return toResponse(emprestimoRepository.save(emp));
    }

    // --- RENOVA UM EMPRÉSTIMO EM ANDAMENTO ---
    @Transactional
    public EmprestimoResponseDTO renovar(UUID emprestimoId) {
        Emprestimo emp = buscarEntidade(emprestimoId);
        ConfiguracaoBiblioteca cfg = configuracaoService.obter();

        if (emp.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new BusinessException("Empréstimo já devolvido não pode ser renovado.");
        }
        if (emp.getRenovacoes() >= cfg.getMaxRenovacoes()) {
            throw new BusinessException("Limite de renovações atingido (" + cfg.getMaxRenovacoes() + ").");
        }
        if (multaRepository.existePendenteDoUsuario(emp.getUsuario().getId())) {
            throw new BusinessException("Usuário possui multa pendente. Renovação bloqueada.");
        }
        long aguardando = reservaRepository.countByLivroIdAndStatus(
                emp.getExemplar().getLivro().getId(), StatusReserva.AGUARDANDO);
        if (aguardando > 0) {
            throw new BusinessException("Existem reservas para este livro. Renovação não permitida.");
        }

        int prazoDias = prazoDias(emp.getUsuario(), cfg);
        LocalDateTime base = emp.getDataDevolucaoPrevista().isBefore(LocalDateTime.now())
                ? LocalDateTime.now()
                : emp.getDataDevolucaoPrevista();
        emp.setDataDevolucaoPrevista(base.plusDays(prazoDias));
        emp.setRenovacoes(emp.getRenovacoes() + 1);
        emp.setStatus(StatusEmprestimo.ATIVO);
        return toResponse(emprestimoRepository.save(emp));
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponseDTO> listarPorUsuario(UUID usuarioId, Pageable pageable) {
        return emprestimoRepository.findByUsuarioId(usuarioId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public EmprestimoResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    // --- JOB DIÁRIO: MARCA COMO ATRASADO EMPRÉSTIMOS COM PRAZO VENCIDO ---
    @Transactional
    public int marcarEmprestimosVencidos() {
        List<Emprestimo> vencidos = emprestimoRepository
                .findByStatusAndDataDevolucaoPrevistaBefore(StatusEmprestimo.ATIVO, LocalDateTime.now());
        vencidos.forEach(e -> e.setStatus(StatusEmprestimo.ATRASADO));
        emprestimoRepository.saveAll(vencidos);
        return vencidos.size();
    }

    // --- HELPERS ---

    private Emprestimo buscarEntidade(UUID id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo", id));
    }

    private Exemplar resolverExemplar(EmprestimoRequestDTO dto) {
        if (dto.getExemplarId() != null) {
            return exemplarRepository.findById(dto.getExemplarId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exemplar", dto.getExemplarId()));
        }
        if (dto.getCodigoBarras() != null && !dto.getCodigoBarras().isBlank()) {
            return exemplarRepository.findByCodigoBarras(dto.getCodigoBarras().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Exemplar", dto.getCodigoBarras()));
        }
        throw new BusinessException("Informe o ID do exemplar ou o código de barras.");
    }

    private int prazoDias(Usuario usuario, ConfiguracaoBiblioteca cfg) {
        return usuario.getRole() == RoleUsuario.PROFESSOR
                ? cfg.getPrazoEmprestimoProfessor()
                : cfg.getPrazoEmprestimoAluno();
    }

    private int calcularDiasAtraso(LocalDateTime previsto, LocalDateTime devolvido) {
        if (!devolvido.isAfter(previsto)) return 0;
        return (int) Math.ceil(Duration.between(previsto, devolvido).toHours() / 24.0);
    }

    // --- GERA A MULTA E DISPARA EVENTO PARA O MÓDULO FINANCEIRO ---
    private void gerarMulta(Emprestimo emp, int diasAtraso) {
        ConfiguracaoBiblioteca cfg = configuracaoService.obter();
        BigDecimal valor = cfg.getValorMultaDia().multiply(BigDecimal.valueOf(diasAtraso));

        Multa multa = Multa.builder()
                .emprestimo(emp)
                .valor(valor)
                .diasAtraso(diasAtraso)
                .status(StatusMulta.PENDENTE)
                .build();
        multa = multaRepository.save(multa);

        eventPublisher.publishEvent(new MultaGeradaEvent(
                multa.getId(),
                emp.getId(),
                emp.getUsuario().getId(),
                multa.getValor(),
                diasAtraso,
                LocalDateTime.now()
        ));
    }

    private void reordenarFila(UUID livroId) {
        List<Reserva> fila = reservaRepository
                .findByLivroIdAndStatusOrderByPosicaoFilaAsc(livroId, StatusReserva.AGUARDANDO);
        int pos = 1;
        for (Reserva r : fila) {
            r.setPosicaoFila(pos++);
        }
        reservaRepository.saveAll(fila);
    }

    private Usuario usuarioAutenticadoOuFalha() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioDetails ud)) {
            throw new BusinessException("Usuário autenticado não identificado.");
        }
        return ud.getUsuario();
    }

    public EmprestimoResponseDTO toResponse(Emprestimo e) {
        int diasAtraso = 0;
        BigDecimal valorMulta = null;
        LocalDateTime baseComparacao = e.getDataDevolucaoEfetiva() != null
                ? e.getDataDevolucaoEfetiva() : LocalDateTime.now();
        if (baseComparacao.isAfter(e.getDataDevolucaoPrevista())) {
            diasAtraso = calcularDiasAtraso(e.getDataDevolucaoPrevista(), baseComparacao);
        }
        Optional<Multa> multaOpt = multaRepository.findAll().stream()
                .filter(m -> m.getEmprestimo().getId().equals(e.getId()))
                .findFirst();
        if (multaOpt.isPresent()) {
            valorMulta = multaOpt.get().getValor();
        }

        return EmprestimoResponseDTO.builder()
                .id(e.getId())
                .exemplarId(e.getExemplar().getId())
                .exemplarCodigoBarras(e.getExemplar().getCodigoBarras())
                .livroId(e.getExemplar().getLivro().getId())
                .livroTitulo(e.getExemplar().getLivro().getTitulo())
                .usuarioId(e.getUsuario().getId())
                .usuarioNome(e.getUsuario().getNome())
                .dataEmprestimo(e.getDataEmprestimo())
                .dataDevolucaoPrevista(e.getDataDevolucaoPrevista())
                .dataDevolucaoEfetiva(e.getDataDevolucaoEfetiva())
                .renovacoes(e.getRenovacoes())
                .status(e.getStatus())
                .valorMulta(valorMulta)
                .diasAtraso(diasAtraso)
                .build();
    }
}

