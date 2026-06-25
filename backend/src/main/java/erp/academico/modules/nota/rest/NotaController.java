package erp.academico.modules.nota.rest;

import erp.academico.modules.nota.dto.BoletimResponseDTO;
import erp.academico.modules.nota.dto.NotaRequestDTO;
import erp.academico.modules.nota.dto.NotaResponseDTO;
import erp.academico.modules.nota.service.NotaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Lançamento, consulta e boletim de notas dos alunos")
public class NotaController {

    private final NotaService notaService;

    // --- LANÇA UMA NOVA NOTA ---
    @PostMapping("/notas")
    @Operation(summary = "Lança uma nova nota (somente o professor responsável, coordenador ou admin)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    public ResponseEntity<NotaResponseDTO> lancar(@Valid @RequestBody NotaRequestDTO dto,
                                                  UriComponentsBuilder uriBuilder) {
        NotaResponseDTO criada = notaService.lancar(dto);
        URI uri = uriBuilder.path("/notas/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    // --- ATUALIZA UMA NOTA EXISTENTE ---
    @PutMapping("/notas/{id}")
    @Operation(summary = "Atualiza uma nota existente (mantém auditoria de quem alterou)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    public ResponseEntity<NotaResponseDTO> atualizar(@PathVariable UUID id,
                                                     @Valid @RequestBody NotaRequestDTO dto) {
        return ResponseEntity.ok(notaService.atualizar(id, dto));
    }

    // --- REMOVE UMA NOTA ---
    @DeleteMapping("/notas/{id}")
    @Operation(summary = "Remove uma nota lançada")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        notaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- TODAS AS NOTAS DA TURMA EM UMA DISCIPLINA ---
    @GetMapping("/turmas/{turmaId}/disciplinas/{disciplinaId}/notas")
    @Operation(summary = "Lista todas as notas de uma turma em uma disciplina")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<List<NotaResponseDTO>> listarPorTurmaDisciplina(@PathVariable UUID turmaId,
                                                                          @PathVariable UUID disciplinaId) {
        return ResponseEntity.ok(notaService.listarPorTurmaDisciplina(turmaId, disciplinaId));
    }

    // --- BOLETIM COMPLETO ---
    @GetMapping("/alunos/{alunoId}/notas")
    @Operation(summary = "Lista todas as notas de um aluno (boletim completo)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<List<NotaResponseDTO>> listarPorAluno(@PathVariable UUID alunoId) {
        return ResponseEntity.ok(notaService.listarPorAluno(alunoId));
    }

    // --- BOLETIM CONSOLIDADO POR PERÍODO LETIVO ---
    @GetMapping("/alunos/{alunoId}/boletim")
    @Operation(summary = "Boletim consolidado do aluno em um período letivo (com média ponderada por disciplina)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<BoletimResponseDTO> boletim(@PathVariable UUID alunoId,
                                                      @RequestParam("periodo") String periodoLetivo) {
        return ResponseEntity.ok(notaService.boletim(alunoId, periodoLetivo));
    }
}

