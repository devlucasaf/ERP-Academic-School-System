package erp.academico.modules.biblioteca.livro.rest;

import erp.academico.modules.biblioteca.livro.dto.LivroRequestDTO;
import erp.academico.modules.biblioteca.livro.dto.LivroResponseDTO;
import erp.academico.modules.biblioteca.livro.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/biblioteca/livros")
@RequiredArgsConstructor
@Tag(name = "Biblioteca - Livros", description = "Acervo de livros da biblioteca")
public class LivroController {

    private final LivroService livroService;

    // --- BUSCA AVANÇADA ---
    @GetMapping
    @Operation(summary = "Busca livros por título, autor, categoria ou ISBN")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<LivroResponseDTO>> buscar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String isbn,
            Pageable pageable) {
        return ResponseEntity.ok(livroService.buscar(titulo, autor, categoria, isbn, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca livro pelo ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LivroResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    // --- CRIA LIVRO COM UPLOAD DE CAPA (OPCIONAL) ---
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Cadastra novo livro no acervo")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<LivroResponseDTO> criar(
            @Valid @RequestPart("dados") LivroRequestDTO dto,
            @RequestPart(value = "capa", required = false) MultipartFile capa,
            UriComponentsBuilder uriBuilder) {
        LivroResponseDTO criado = livroService.criar(dto, capa);
        URI uri = uriBuilder.path("/biblioteca/livros/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Atualiza livro do acervo")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<LivroResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestPart("dados") LivroRequestDTO dto,
            @RequestPart(value = "capa", required = false) MultipartFile capa) {
        return ResponseEntity.ok(livroService.atualizar(id, dto, capa));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove livro do acervo (sem exemplares)")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

