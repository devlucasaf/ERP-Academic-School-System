package erp.academico.modules.turma.rest;

import erp.academico.modules.aluno.dto.AlunoResponseDTO;
import erp.academico.modules.turma.dto.TurmaDisciplinaResponseDTO;
import erp.academico.modules.turma.dto.TurmaRequestDTO;
import erp.academico.modules.turma.dto.TurmaResponseDTO;
import erp.academico.modules.turma.dto.VincularDisciplinaTurmaRequestDTO;
import erp.academico.modules.turma.service.TurmaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/turmas")
@RequiredArgsConstructor
@Tag(name = "Turmas", description = "CRUD de turmas, vínculos com disciplinas e listagem de alunos")
public class TurmaController {

    private final TurmaService turmaService;

    // --- LISTA TURMAS ---
    @GetMapping
    @Operation(summary = "Lista turmas paginadas, opcionalmente filtrando por curso e/ou período letivo")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<Page<TurmaResponseDTO>> listar(
            @RequestParam(required = false) UUID cursoId,
            @RequestParam(required = false) String periodoLetivo,
            Pageable pageable) {
        return ResponseEntity.ok(turmaService.listar(cursoId, periodoLetivo, pageable));
    }

    // --- BUSCA TURMA PELO ID ---
    @GetMapping("/{id}")
    @Operation(summary = "Busca turma pelo ID")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<TurmaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(turmaService.buscarPorId(id));
    }

    // --- BUSCA TURMA PELO CÓDIGO ÚNICO ---
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Busca turma pelo código único")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<TurmaResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(turmaService.buscarPorCodigo(codigo));
    }

    // --- CRIA UMA NOVA TURMA ---
    @PostMapping
    @Operation(summary = "Cria uma nova turma")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<TurmaResponseDTO> criar(@Valid @RequestBody TurmaRequestDTO dto,
                                                  UriComponentsBuilder uriBuilder) {
        TurmaResponseDTO criada = turmaService.criar(dto);
        // --- MONTA A URI DO RECURSO CRIADO PARA O HEADER LOCATION ---
        URI uri = uriBuilder.path("/turmas/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    // --- ATUALIZA UMA TURMA EXISTENTE ---
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma turma existente")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<TurmaResponseDTO> atualizar(@PathVariable UUID id,
                                                      @Valid @RequestBody TurmaRequestDTO dto) {
        return ResponseEntity.ok(turmaService.atualizar(id, dto));
    }

    // --- REMOVE UMA TURMA ---
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma turma")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        turmaService.deletar(id);
        return ResponseEntity.noContent().build();
    }


    // --- LISTA OS ALUNOS MATRICULADOS NA TURMA ---
    @GetMapping("/{id}/alunos")
    @Operation(summary = "Lista os alunos matriculados na turma")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<Page<AlunoResponseDTO>> listarAlunos(@PathVariable UUID id,
                                                               Pageable pageable) {
        return ResponseEntity.ok(turmaService.listarAlunos(id, pageable));
    }

    // --- LISTA AS DISCIPLINAS DA TURMA ---
    @GetMapping("/{id}/disciplinas")
    @Operation(summary = "Lista as disciplinas vinculadas à turma")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<List<TurmaDisciplinaResponseDTO>> listarDisciplinas(@PathVariable UUID id) {
        return ResponseEntity.ok(turmaService.listarDisciplinas(id));
    }

    // --- ADICIONA UMA DISCIPLINA À TURMA COM PROFESSOR E HORÁRIO ---
    @PostMapping("/{id}/disciplinas")
    @Operation(summary = "Adiciona uma disciplina à turma (com professor responsável e horário)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<TurmaDisciplinaResponseDTO> vincularDisciplina(
            @PathVariable UUID id,
            @Valid @RequestBody VincularDisciplinaTurmaRequestDTO dto) {
        return ResponseEntity.status(201).body(turmaService.vincularDisciplina(id, dto));
    }

    // --- REMOVE O VÍNCULO DE UMA DISCIPLINA NA TURMA ---
    @DeleteMapping("/{id}/disciplinas/{disciplinaId}")
    @Operation(summary = "Remove o vínculo de uma disciplina na turma")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<Void> desvincularDisciplina(@PathVariable UUID id, @PathVariable UUID disciplinaId) {
        turmaService.desvincularDisciplina(id, disciplinaId);
        return ResponseEntity.noContent().build();
    }
}

