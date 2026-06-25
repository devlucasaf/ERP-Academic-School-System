package erp.academico.modules.frequencia.rest;

import erp.academico.modules.frequencia.dto.AulaRequestDTO;
import erp.academico.modules.frequencia.dto.AulaResponseDTO;
import erp.academico.modules.frequencia.dto.ChamadaRequestDTO;
import erp.academico.modules.frequencia.dto.FrequenciaPercentualDTO;
import erp.academico.modules.frequencia.dto.FrequenciaResponseDTO;
import erp.academico.modules.frequencia.dto.FrequenciaResumoDTO;
import erp.academico.modules.frequencia.service.FrequenciaService;

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
@Tag(name = "Frequência", description = "Aulas, chamada e cálculo de frequência dos alunos")
public class FrequenciaController {

    private final FrequenciaService frequenciaService;

    // --- CRIA UMA AULA E GERA AS FREQUÊNCIAS AUTOMATICAMENTE ---
    @PostMapping("/aulas")
    @Operation(summary = "Cria uma aula e gera registros de frequência (presente=true) para todos os alunos matriculados")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    public ResponseEntity<AulaResponseDTO> criarAula(@Valid @RequestBody AulaRequestDTO dto,
                                                     UriComponentsBuilder uriBuilder) {
        AulaResponseDTO criada = frequenciaService.criarAula(dto);
        URI uri = uriBuilder.path("/aulas/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    // --- LISTA OS REGISTROS DE PRESENÇA DA AULA ---
    @GetMapping("/aulas/{id}/frequencias")
    @Operation(summary = "Lista os registros de presença/falta da aula")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<List<FrequenciaResponseDTO>> listarFrequencias(@PathVariable UUID id) {
        return ResponseEntity.ok(frequenciaService.listarFrequenciasDaAula(id));
    }

    // --- REGISTRA PRESENÇAS/FALTAS EM LOTE ---
    @PutMapping("/aulas/{id}/chamada")
    @Operation(summary = "Registra presenças e faltas da aula em lote")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    public ResponseEntity<List<FrequenciaResponseDTO>> registrarChamada(@PathVariable UUID id,
                                                                        @Valid @RequestBody ChamadaRequestDTO dto) {
        return ResponseEntity.ok(frequenciaService.registrarChamada(id, dto));
    }

    // --- PERCENTUAL DO ALUNO EM UMA DISCIPLINA ---
    @GetMapping("/alunos/{alunoId}/frequencia")
    @Operation(summary = "Percentual de frequência do aluno em uma disciplina específica")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<FrequenciaPercentualDTO> percentualPorDisciplina(
            @PathVariable UUID alunoId,
            @RequestParam("disciplina") UUID disciplinaId) {
        return ResponseEntity.ok(frequenciaService.calcularPercentual(alunoId, disciplinaId));
    }

    // --- PERCENTUAL POR TODAS AS DISCIPLINAS DO ALUNO ---
    @GetMapping("/alunos/{alunoId}/frequencia/resumo")
    @Operation(summary = "Resumo geral do aluno (percentual por disciplina)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<FrequenciaResumoDTO> resumo(@PathVariable UUID alunoId) {
        return ResponseEntity.ok(frequenciaService.resumoDoAluno(alunoId));
    }
}

