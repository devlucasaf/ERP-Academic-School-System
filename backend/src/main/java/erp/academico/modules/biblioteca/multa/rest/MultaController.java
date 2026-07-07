package erp.academico.modules.biblioteca.multa.rest;

import erp.academico.modules.biblioteca.multa.dto.MultaResponseDTO;
import erp.academico.modules.biblioteca.multa.model.StatusMulta;
import erp.academico.modules.biblioteca.multa.service.MultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/biblioteca/multas")
@RequiredArgsConstructor
@Tag(name = "Biblioteca - Multas", description = "Multas geradas por atrasos")
public class MultaController {

    private final MultaService multaService;

    @GetMapping
    @Operation(summary = "Lista multas por status")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN','FINANCEIRO')")
    public ResponseEntity<Page<MultaResponseDTO>> listar(
            @RequestParam(defaultValue = "PENDENTE") StatusMulta status,
            Pageable pageable) {
        return ResponseEntity.ok(multaService.listarPorStatus(status, pageable));
    }

    @GetMapping("/usuario/{usuarioId}/pendentes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MultaResponseDTO>> pendentesDoUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(multaService.pendentesDoUsuario(usuarioId));
    }

    @PostMapping("/{id}/pagar")
    @Operation(summary = "Registra pagamento da multa")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN','FINANCEIRO')")
    public ResponseEntity<MultaResponseDTO> pagar(@PathVariable UUID id) {
        return ResponseEntity.ok(multaService.pagar(id));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MultaResponseDTO> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(multaService.cancelar(id));
    }
}

