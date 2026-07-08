package erp.academico.modules.professor.rest;

import erp.academico.modules.professor.dto.ProfessorDisciplinaResponseDTO;
import erp.academico.modules.professor.dto.ProfessorRequestDTO;
import erp.academico.modules.professor.dto.ProfessorResponseDTO;
import erp.academico.modules.professor.dto.VincularDisciplinaRequestDTO;
import erp.academico.modules.professor.service.ProfessorService;

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
@RequestMapping("/professores")
@RequiredArgsConstructor
@Tag(name = "Professores", description = "CRUD de professores e vínculos com disciplinas")
public class ProfessorController {

    private final ProfessorService professorService;

    // --- LISTA PROFESSORES PAGINADOS ---
    @GetMapping
    @Operation(summary = "Lista professores paginados")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<Page<ProfessorResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(professorService.listar(pageable));
    }

    // --- BUSCA PROFESSOR PELO ID ---
    @GetMapping("/{id}")
    @Operation(summary = "Busca professor pelo ID")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<ProfessorResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(professorService.buscarPorId(id));
    }

    // --- CRIA UM PROFESSOR ---
    @PostMapping
    @Operation(summary = "Cria um professor (cria também o usuário associado com role PROFESSOR)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<ProfessorResponseDTO> criar(@Valid @RequestBody ProfessorRequestDTO dto,
                                                      UriComponentsBuilder uriBuilder) {
        ProfessorResponseDTO criado = professorService.criar(dto);
        URI uri = uriBuilder.path("/professores/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    // --- ATUALIZA UM PROFESSOR EXISTENTE ---
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um professor existente")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<ProfessorResponseDTO> atualizar(@PathVariable UUID id,
                                                          @Valid @RequestBody ProfessorRequestDTO dto) {
        return ResponseEntity.ok(professorService.atualizar(id, dto));
    }

    // --- REMOVE UM PROFESSOR ---
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um professor")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        professorService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- LISTA AS DISCIPLINAS LECIONADAS PELO PROFESSOR ---
    @GetMapping("/{id}/disciplinas")
    @Operation(summary = "Lista as disciplinas lecionadas pelo professor")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<List<ProfessorDisciplinaResponseDTO>> listarDisciplinas(@PathVariable UUID id) {
        return ResponseEntity.ok(professorService.listarDisciplinas(id));
    }

    // --- VINCULA UMA DISCIPLINA AO PROFESSOR ---
    @PostMapping("/{id}/disciplinas")
    @Operation(summary = "Vincula uma disciplina ao professor")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<ProfessorDisciplinaResponseDTO> vincularDisciplina(
            @PathVariable UUID id,
            @Valid @RequestBody VincularDisciplinaRequestDTO dto) {
        return ResponseEntity.status(201).body(professorService.vincularDisciplina(id, dto));
    }

    // --- REMOVE O VÍNCULO ENTRE PROFESSOR E DISCIPLINA ---
    @DeleteMapping("/{id}/disciplinas/{disciplinaId}")
    @Operation(summary = "Remove o vínculo entre professor e disciplina")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<Void> desvincularDisciplina(@PathVariable UUID id,
                                                      @PathVariable UUID disciplinaId) {
        professorService.desvincularDisciplina(id, disciplinaId);
        return ResponseEntity.noContent().build();
    }
}
