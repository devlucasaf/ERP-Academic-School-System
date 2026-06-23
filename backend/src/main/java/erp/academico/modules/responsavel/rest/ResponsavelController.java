package erp.academico.modules.responsavel.rest;

import erp.academico.modules.responsavel.dto.ResponsavelAlunoResponseDTO;
import erp.academico.modules.responsavel.dto.ResponsavelRequestDTO;
import erp.academico.modules.responsavel.dto.ResponsavelResponseDTO;
import erp.academico.modules.responsavel.dto.VincularAlunoRequestDTO;
import erp.academico.modules.responsavel.service.ResponsavelService;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/responsaveis")
@RequiredArgsConstructor
@Tag(name = "Responsáveis", description = "CRUD de responsáveis e vínculos com alunos")
public class ResponsavelController {

    private final ResponsavelService responsavelService;

    @GetMapping
    @Operation(summary = "Lista responsáveis paginados")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<Page<ResponsavelResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(responsavelService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca responsável pelo ID")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<ResponsavelResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(responsavelService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cria um responsável (cria também o usuário associado com role RESPONSAVEL)")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ResponsavelResponseDTO> criar(@Valid @RequestBody ResponsavelRequestDTO dto,
                                                        UriComponentsBuilder uriBuilder) {
        ResponsavelResponseDTO criado = responsavelService.criar(dto);
        URI uri = uriBuilder.path("/responsaveis/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um responsável existente")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ResponsavelResponseDTO> atualizar(@PathVariable UUID id,
                                                            @Valid @RequestBody ResponsavelRequestDTO dto) {
        return ResponseEntity.ok(responsavelService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um responsável")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        responsavelService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- VÍNCULOS RESPONSÁVEL ---

    @GetMapping("/{id}/alunos")
    @Operation(summary = "Lista os alunos vinculados ao responsável")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<List<ResponsavelAlunoResponseDTO>> listarAlunos(@PathVariable UUID id) {
        return ResponseEntity.ok(responsavelService.listarAlunos(id));
    }

    @PostMapping("/{id}/alunos")
    @Operation(summary = "Vincula um aluno ao responsável")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<ResponsavelAlunoResponseDTO> vincularAluno(
            @PathVariable UUID id,
            @Valid @RequestBody VincularAlunoRequestDTO dto) {
        return ResponseEntity.status(201).body(responsavelService.vincularAluno(id, dto));
    }

    @DeleteMapping("/{id}/alunos/{alunoId}")
    @Operation(summary = "Remove o vínculo entre responsável e aluno")
    @PreAuthorize("hasAnyRole('ADMIN','SECRETARIA')")
    public ResponseEntity<Void> desvincularAluno(@PathVariable UUID id,
                                                 @PathVariable UUID alunoId) {
        responsavelService.desvincularAluno(id, alunoId);
        return ResponseEntity.noContent().build();
    }
}

