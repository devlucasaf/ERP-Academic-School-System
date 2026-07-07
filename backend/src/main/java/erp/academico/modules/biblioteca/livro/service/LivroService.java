package erp.academico.modules.biblioteca.livro.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.infra.storage.StorageService;
import erp.academico.modules.biblioteca.exemplar.model.StatusExemplar;
import erp.academico.modules.biblioteca.exemplar.repository.ExemplarRepository;
import erp.academico.modules.biblioteca.livro.dto.LivroRequestDTO;
import erp.academico.modules.biblioteca.livro.dto.LivroResponseDTO;
import erp.academico.modules.biblioteca.livro.model.Livro;
import erp.academico.modules.biblioteca.livro.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LivroService {

    private static final String SUBDIR_CAPAS = "biblioteca/capas";

    private final LivroRepository livroRepository;
    private final ExemplarRepository exemplarRepository;
    private final StorageService storageService;

    // --- LISTA / BUSCA AVANÇADA ---
    @Transactional(readOnly = true)
    public Page<LivroResponseDTO> buscar(String titulo, String autor, String categoria, String isbn, Pageable pageable) {
        return livroRepository.buscar(
                normalizar(titulo),
                normalizar(autor),
                normalizar(categoria),
                normalizar(isbn),
                pageable
        ).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public LivroResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    // --- CRIA LIVRO ---
    @Transactional
    public LivroResponseDTO criar(LivroRequestDTO dto, MultipartFile capa) {
        validarIsbnUnico(dto.getIsbn(), null);

        Livro livro = Livro.builder()
                .isbn(dto.getIsbn())
                .titulo(dto.getTitulo())
                .autor(dto.getAutor())
                .editora(dto.getEditora())
                .anoPublicacao(dto.getAnoPublicacao())
                .edicao(dto.getEdicao())
                .paginas(dto.getPaginas())
                .categoria(dto.getCategoria())
                .sinopse(dto.getSinopse())
                .build();

        if (capa != null && !capa.isEmpty()) {
            livro.setCapaUrl(storageService.store(capa, SUBDIR_CAPAS));
        }

        return toResponse(livroRepository.save(livro));
    }

    // --- ATUALIZA LIVRO ---
    @Transactional
    public LivroResponseDTO atualizar(UUID id, LivroRequestDTO dto, MultipartFile capa) {
        Livro livro = buscarEntidade(id);
        validarIsbnUnico(dto.getIsbn(), id);

        livro.setIsbn(dto.getIsbn());
        livro.setTitulo(dto.getTitulo());
        livro.setAutor(dto.getAutor());
        livro.setEditora(dto.getEditora());
        livro.setAnoPublicacao(dto.getAnoPublicacao());
        livro.setEdicao(dto.getEdicao());
        livro.setPaginas(dto.getPaginas());
        livro.setCategoria(dto.getCategoria());
        livro.setSinopse(dto.getSinopse());

        if (capa != null && !capa.isEmpty()) {
            if (livro.getCapaUrl() != null) {
                storageService.delete(livro.getCapaUrl());
            }
            livro.setCapaUrl(storageService.store(capa, SUBDIR_CAPAS));
        }

        return toResponse(livroRepository.save(livro));
    }

    // --- REMOVE LIVRO ---
    @Transactional
    public void deletar(UUID id) {
        Livro livro = buscarEntidade(id);
        if (exemplarRepository.countByLivroId(id) > 0) {
            throw new BusinessException("Não é possível excluir livro com exemplares cadastrados.");
        }

        if (livro.getCapaUrl() != null) {
            storageService.delete(livro.getCapaUrl());
        }
        livroRepository.delete(livro);
    }

    // --- HELPERS ---
    public Livro buscarEntidade(UUID id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro", id));
    }

    private void validarIsbnUnico(String isbn, UUID idAtual) {
        if (isbn == null || isbn.isBlank()) {
            return;
        }
        livroRepository.findByIsbn(isbn).ifPresent(existente -> {
            if (idAtual == null || !existente.getId().equals(idAtual)) {
                throw new BusinessException("Já existe um livro cadastrado com o ISBN informado.");
            }
        });
    }

    private String normalizar(String v) {
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private LivroResponseDTO toResponse(Livro l) {
        long total = exemplarRepository.countByLivroId(l.getId());
        long disponiveis = exemplarRepository.countByLivroIdAndStatus(l.getId(), StatusExemplar.DISPONIVEL);
        return LivroResponseDTO.builder()
                .id(l.getId())
                .isbn(l.getIsbn())
                .titulo(l.getTitulo())
                .autor(l.getAutor())
                .editora(l.getEditora())
                .anoPublicacao(l.getAnoPublicacao())
                .edicao(l.getEdicao())
                .paginas(l.getPaginas())
                .categoria(l.getCategoria())
                .sinopse(l.getSinopse())
                .capaUrl(l.getCapaUrl())
                .totalExemplares(total)
                .exemplaresDisponiveis(disponiveis)
                .criadoEm(l.getCriadoEm())
                .atualizadoEm(l.getAtualizadoEm())
                .build();
    }
}

