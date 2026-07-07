package erp.academico.modules.biblioteca.exemplar.rest;

import erp.academico.modules.biblioteca.exemplar.dto.ExemplarRequestDTO;
import erp.academico.modules.biblioteca.exemplar.dto.ExemplarResponseDTO;
import erp.academico.modules.biblioteca.exemplar.model.StatusExemplar;
import erp.academico.modules.biblioteca.exemplar.service.ExemplarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/biblioteca/exemplares")
@RequiredArgsConstructor
@Tag(name = "Biblioteca - Exemplares", description = "Exemplares físicos dos livros")
public class ExemplarController {

    private final ExemplarService exemplarService;

    @GetMapping("/livro/{livroId}")
    @Operation(summary = "Lista exemplares de um livro")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ExemplarResponseDTO>> listarPorLivro(@PathVariable UUID livroId, Pageable pageable) {
        return ResponseEntity.ok(exemplarService.listarPorLivro(livroId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExemplarResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(exemplarService.buscarPorId(id));
    }

    @GetMapping("/codigo/{codigoBarras}")
    @Operation(summary = "Busca exemplar pelo código de barras")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<ExemplarResponseDTO> buscarPorCodigo(@PathVariable String codigoBarras) {
        return ResponseEntity.ok(exemplarService.buscarPorCodigoBarras(codigoBarras));
    }

    // --- ENDPOINT AUXILIAR PARA GERAR CÓDIGO DE BARRAS AUTOMATICAMENTE ---
    @GetMapping("/gerar-codigo-barras")
    @Operation(summary = "Gera um código de barras único sugerido")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<Map<String, String>> gerarCodigoBarras() {
        return ResponseEntity.ok(Map.of("codigoBarras", exemplarService.gerarCodigoBarras()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<ExemplarResponseDTO> criar(@Valid @RequestBody ExemplarRequestDTO dto) {
        return ResponseEntity.ok(exemplarService.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<ExemplarResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ExemplarRequestDTO dto,
            @RequestParam(required = false) StatusExemplar status) {
        return ResponseEntity.ok(exemplarService.atualizar(id, dto, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        exemplarService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

