package erp.academico.modules.biblioteca.reserva.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.infra.security.UsuarioDetails;
import erp.academico.modules.biblioteca.exemplar.model.StatusExemplar;
import erp.academico.modules.biblioteca.exemplar.repository.ExemplarRepository;
import erp.academico.modules.biblioteca.livro.model.Livro;
import erp.academico.modules.biblioteca.livro.service.LivroService;
import erp.academico.modules.biblioteca.reserva.dto.ReservaRequestDTO;
import erp.academico.modules.biblioteca.reserva.dto.ReservaResponseDTO;
import erp.academico.modules.biblioteca.reserva.model.Reserva;
import erp.academico.modules.biblioteca.reserva.model.StatusReserva;
import erp.academico.modules.biblioteca.reserva.repository.ReservaRepository;
import erp.academico.modules.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ExemplarRepository exemplarRepository;
    private final LivroService livroService;

    // --- CRIA UMA RESERVA PARA O USUÁRIO AUTENTICADO ---
    @Transactional
    public ReservaResponseDTO reservar(ReservaRequestDTO dto) {
        Livro livro = livroService.buscarEntidade(dto.getLivroId());
        Usuario autenticado = usuarioAutenticadoOuFalha();

        // --- SE HÁ EXEMPLAR DISPONÍVEL, NÃO PRECISA RESERVAR ---
        long disponiveis = exemplarRepository.countByLivroIdAndStatus(livro.getId(), StatusExemplar.DISPONIVEL);
        if (disponiveis > 0) {
            throw new BusinessException("Existe(m) exemplar(es) disponível(is) para este livro. Faça o empréstimo diretamente.");
        }

        if (reservaRepository.existsByLivroIdAndUsuarioIdAndStatus(livro.getId(), autenticado.getId(), StatusReserva.AGUARDANDO)) {
            throw new BusinessException("Você já possui uma reserva ativa para este livro.");
        }

        int posicao = (int) reservaRepository.countByLivroIdAndStatus(livro.getId(), StatusReserva.AGUARDANDO) + 1;

        Reserva reserva = Reserva.builder()
                .livro(livro)
                .usuario(autenticado)
                .status(StatusReserva.AGUARDANDO)
                .posicaoFila(posicao)
                .build();
        return toResponse(reservaRepository.save(reserva));
    }

    // --- CANCELA UMA RESERVA E REORDENA A FILA ---
    @Transactional
    public void cancelar(UUID id) {
        Reserva r = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));

        Usuario autenticado = usuarioAutenticadoOuFalha();
        if (!r.getUsuario().getId().equals(autenticado.getId())
                && autenticado.getRole().name().equals("ALUNO")) {
            throw new BusinessException("Você não pode cancelar reservas de outro usuário.");
        }

        r.setStatus(StatusReserva.CANCELADA);
        reservaRepository.save(r);
        reordenarFila(r.getLivro().getId());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> filaDoLivro(UUID livroId) {
        return reservaRepository
                .findByLivroIdAndStatusOrderByPosicaoFilaAsc(livroId, StatusReserva.AGUARDANDO)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> reservasDoUsuario(UUID usuarioId) {
        return reservaRepository.findByUsuarioIdOrderByDataReservaDesc(usuarioId)
                .stream().map(this::toResponse).toList();
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

    private ReservaResponseDTO toResponse(Reserva r) {
        return ReservaResponseDTO.builder()
                .id(r.getId())
                .livroId(r.getLivro().getId())
                .livroTitulo(r.getLivro().getTitulo())
                .usuarioId(r.getUsuario().getId())
                .usuarioNome(r.getUsuario().getNome())
                .dataReserva(r.getDataReserva())
                .status(r.getStatus())
                .posicaoFila(r.getPosicaoFila())
                .build();
    }
}

