package erp.academico.modules.matricula.rest;

import erp.academico.modules.matricula.dto.AlterarStatusMatriculaRequestDTO;
import erp.academico.modules.matricula.dto.MatriculaRequestDTO;
import erp.academico.modules.matricula.dto.MatriculaResponseDTO;
import erp.academico.modules.matricula.service.MatriculaService;

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
import java.util.UUID;

@RestController
@RequestMapping("/matriculas")
@RequiredArgsConstructor
@Tag(name = "Matrículas", description = "Matrícula de alunos em turmas e mudança de status (trancar/cancelar)")
public class MatriculaController {

    private final MatriculaService matriculaService;

    // --- LISTA MATRÍCULAS ---
    @GetMapping
    @Operation(summary = "Lista matrículas (filtro opcional ?aluno={id} retorna o histórico do aluno)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<Page<MatriculaResponseDTO>> listar(
            @RequestParam(value = "aluno", required = false) UUID alunoId,
            Pageable pageable) {
        return ResponseEntity.ok(matriculaService.listar(alunoId, pageable));
    }

    // --- BUSCA MATRÍCULA POR ID ---
    @GetMapping("/{id}")
    @Operation(summary = "Busca matrícula pelo ID")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<MatriculaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(matriculaService.buscarPorId(id));
    }

    // --- MATRICULA UM ALUNO EM UMA TURMA ---
    @PostMapping
    @Operation(summary = "Matricula um aluno em uma turma")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<MatriculaResponseDTO> matricular(@Valid @RequestBody MatriculaRequestDTO dto,
                                                           UriComponentsBuilder uriBuilder) {
        MatriculaResponseDTO criada = matriculaService.matricular(dto);
        URI uri = uriBuilder.path("/matriculas/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    // --- TRANCA UMA MATRÍCULA ATIVA ---
    @PutMapping("/{id}/trancar")
    @Operation(summary = "Tranca a matrícula (libera vaga e remove vínculo de turma atual do aluno)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<MatriculaResponseDTO> trancar(
            @PathVariable UUID id,
            @RequestBody(required = false) AlterarStatusMatriculaRequestDTO dto) {
        return ResponseEntity.ok(matriculaService.trancar(id, dto));
    }

    // --- CANCELA UMA MATRÍCULA ATIVA ---
    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancela a matrícula (libera vaga e remove vínculo de turma atual do aluno)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA')")
    public ResponseEntity<MatriculaResponseDTO> cancelar(
            @PathVariable UUID id,
            @RequestBody(required = false) AlterarStatusMatriculaRequestDTO dto) {
        return ResponseEntity.ok(matriculaService.cancelar(id, dto));
    }
}

