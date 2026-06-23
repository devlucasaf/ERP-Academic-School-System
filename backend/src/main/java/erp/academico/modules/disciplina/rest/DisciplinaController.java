package erp.academico.modules.disciplina.rest;

import erp.academico.modules.disciplina.dto.DisciplinaPrerequisitoResponseDTO;
import erp.academico.modules.disciplina.dto.DisciplinaRequestDTO;
import erp.academico.modules.disciplina.dto.DisciplinaResponseDTO;
import erp.academico.modules.disciplina.dto.VincularPrerequisitoRequestDTO;
import erp.academico.modules.disciplina.service.DisciplinaService;
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
@RequestMapping("/disciplinas")
@RequiredArgsConstructor
@Tag(name = "Disciplinas", description = "CRUD de disciplinas e pré-requisitos")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    // --- LISTA PAGINADA DE DISCIPLINAS ---
    @GetMapping
    @Operation(summary = "Lista disciplinas paginadas")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<Page<DisciplinaResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(disciplinaService.listar(pageable));
    }

    // --- BUSCA POR ID ---
    @GetMapping("/{id}")
    @Operation(summary = "Busca disciplina pelo ID")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<DisciplinaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(disciplinaService.buscarPorId(id));
    }

    // --- BUSCA POR CÓDIGO ---
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Busca disciplina pelo código")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<DisciplinaResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(disciplinaService.buscarPorCodigo(codigo));
    }

    // --- CRIA UMA NOVA DISCIPLINA ---
    @PostMapping
    @Operation(summary = "Cria uma nova disciplina")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<DisciplinaResponseDTO> criar(@Valid @RequestBody DisciplinaRequestDTO dto,
                                                       UriComponentsBuilder uriBuilder) {
        DisciplinaResponseDTO criada = disciplinaService.criar(dto);
        URI uri = uriBuilder.path("/disciplinas/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    // --- ATUALIZA UMA DISCIPLINA EXISTENTE ---
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma disciplina existente")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<DisciplinaResponseDTO> atualizar(@PathVariable UUID id,
                                                           @Valid @RequestBody DisciplinaRequestDTO dto) {
        return ResponseEntity.ok(disciplinaService.atualizar(id, dto));
    }

    // --- REMOVE A DISCIPLINA ---
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma disciplina")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        disciplinaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- LISTA OS PRÉ-REQUISITOS ---
    @GetMapping("/{id}/prerequisitos")
    @Operation(summary = "Lista os pré-requisitos da disciplina")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<List<DisciplinaPrerequisitoResponseDTO>> listarPrerequisitos(@PathVariable UUID id) {
        return ResponseEntity.ok(disciplinaService.listarPrerequisitos(id));
    }

    // --- ADICIONA UM PRÉ-REQUISITO ---
    @PostMapping("/{id}/prerequisitos")
    @Operation(summary = "Adiciona um pré-requisito à disciplina")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<DisciplinaPrerequisitoResponseDTO> vincularPrerequisito(@PathVariable UUID id,
            @Valid @RequestBody VincularPrerequisitoRequestDTO dto) {
        return ResponseEntity.status(201).body(disciplinaService.vincularPrerequisito(id, dto));
    }

    // --- REMOVE O VÍNCULO (204) ---
    @DeleteMapping("/{id}/prerequisitos/{prerequisitoId}")
    @Operation(summary = "Remove o vínculo de pré-requisito")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<Void> desvincularPrerequisito(@PathVariable UUID id, @PathVariable UUID prerequisitoId) {
        disciplinaService.desvincularPrerequisito(id, prerequisitoId);
        return ResponseEntity.noContent().build();
    }
}
