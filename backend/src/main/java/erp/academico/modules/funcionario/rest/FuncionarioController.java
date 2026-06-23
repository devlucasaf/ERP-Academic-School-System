package erp.academico.modules.funcionario.rest;

import erp.academico.modules.funcionario.dto.FuncionarioRequestDTO;
import erp.academico.modules.funcionario.dto.FuncionarioResponseDTO;
import erp.academico.modules.funcionario.model.CargoFuncionario;
import erp.academico.modules.funcionario.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/funcionarios")
@RequiredArgsConstructor
@Tag(name = "Funcionários", description = "CRUD de funcionários administrativos")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @GetMapping
    @Operation(summary = "Lista funcionários paginados (opcionalmente filtrando por cargo)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<Page<FuncionarioResponseDTO>> listar(
            @RequestParam(required = false) CargoFuncionario cargo,
            Pageable pageable) {
        return ResponseEntity.ok(funcionarioService.listar(cargo, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca funcionário pelo ID")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(funcionarioService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cria um funcionário (cria também o usuário com a role correspondente ao cargo)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> criar(@Valid @RequestBody FuncionarioRequestDTO dto,
                                                        UriComponentsBuilder uriBuilder) {
        FuncionarioResponseDTO criado = funcionarioService.criar(dto);
        URI uri = uriBuilder.path("/funcionarios/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um funcionário existente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FuncionarioResponseDTO> atualizar(@PathVariable UUID id,
                                                            @Valid @RequestBody FuncionarioRequestDTO dto) {
        return ResponseEntity.ok(funcionarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um funcionário")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

