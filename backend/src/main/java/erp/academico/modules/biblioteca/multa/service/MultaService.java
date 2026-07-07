package erp.academico.modules.biblioteca.multa.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.modules.biblioteca.multa.dto.MultaResponseDTO;
import erp.academico.modules.biblioteca.multa.model.Multa;
import erp.academico.modules.biblioteca.multa.model.StatusMulta;
import erp.academico.modules.biblioteca.multa.repository.MultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MultaService {

    private final MultaRepository multaRepository;

    @Transactional(readOnly = true)
    public Page<MultaResponseDTO> listarPorStatus(StatusMulta status, Pageable pageable) {
        return multaRepository.findByStatus(status, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<MultaResponseDTO> pendentesDoUsuario(UUID usuarioId) {
        return multaRepository.findByEmprestimoUsuarioIdAndStatus(usuarioId, StatusMulta.PENDENTE)
                .stream().map(this::toResponse).toList();
    }

    // --- BAIXA DE PAGAMENTO ---
    @Transactional
    public MultaResponseDTO pagar(UUID multaId) {
        Multa m = multaRepository.findById(multaId)
                .orElseThrow(() -> new ResourceNotFoundException("Multa", multaId));
        if (m.getStatus() != StatusMulta.PENDENTE) {
            throw new BusinessException("Somente multas PENDENTES podem ser pagas.");
        }

        m.setStatus(StatusMulta.PAGA);
        m.setPagaEm(LocalDateTime.now());
        return toResponse(multaRepository.save(m));
    }

    @Transactional
    public MultaResponseDTO cancelar(UUID multaId) {
        Multa m = multaRepository.findById(multaId)
                .orElseThrow(() -> new ResourceNotFoundException("Multa", multaId));
        if (m.getStatus() == StatusMulta.PAGA) {
            throw new BusinessException("Não é possível cancelar multa já paga.");
        }

        m.setStatus(StatusMulta.CANCELADA);
        return toResponse(multaRepository.save(m));
    }

    private MultaResponseDTO toResponse(Multa m) {
        return MultaResponseDTO.builder()
                .id(m.getId())
                .emprestimoId(m.getEmprestimo().getId())
                .usuarioId(m.getEmprestimo().getUsuario().getId())
                .usuarioNome(m.getEmprestimo().getUsuario().getNome())
                .livroTitulo(m.getEmprestimo().getExemplar().getLivro().getTitulo())
                .valor(m.getValor())
                .diasAtraso(m.getDiasAtraso())
                .status(m.getStatus())
                .geradaEm(m.getGeradaEm())
                .pagaEm(m.getPagaEm())
                .build();
    }
}

