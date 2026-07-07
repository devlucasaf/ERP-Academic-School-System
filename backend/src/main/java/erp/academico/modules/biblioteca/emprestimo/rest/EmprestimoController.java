package erp.academico.modules.biblioteca.emprestimo.rest;

import erp.academico.modules.biblioteca.emprestimo.dto.EmprestimoRequestDTO;
import erp.academico.modules.biblioteca.emprestimo.dto.EmprestimoResponseDTO;
import erp.academico.modules.biblioteca.emprestimo.service.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/biblioteca/emprestimos")
@RequiredArgsConstructor
@Tag(name = "Biblioteca - Empréstimos", description = "Empréstimos, devoluções e renovações")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    @Operation(summary = "Registra um novo empréstimo")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<EmprestimoResponseDTO> registrar(@Valid @RequestBody EmprestimoRequestDTO dto) {
        return ResponseEntity.ok(emprestimoService.registrar(dto));
    }

    @PostMapping("/{id}/devolver")
    @Operation(summary = "Registra devolução do empréstimo (gera multa se atrasado)")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<EmprestimoResponseDTO> devolver(@PathVariable UUID id) {
        return ResponseEntity.ok(emprestimoService.devolver(id));
    }

    @PostMapping("/{id}/renovar")
    @Operation(summary = "Renova empréstimo respeitando limite e fila de reservas")
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN','ALUNO','PROFESSOR')")
    public ResponseEntity<EmprestimoResponseDTO> renovar(@PathVariable UUID id) {
        return ResponseEntity.ok(emprestimoService.renovar(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmprestimoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(emprestimoService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Lista empréstimos de um usuário")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<EmprestimoResponseDTO>> listarPorUsuario(@PathVariable UUID usuarioId,
                                                                        Pageable pageable) {
        return ResponseEntity.ok(emprestimoService.listarPorUsuario(usuarioId, pageable));
    }
}

