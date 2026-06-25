package erp.academico.modules.material.rest;

import erp.academico.modules.material.dto.MaterialRequestDTO;
import erp.academico.modules.material.dto.MaterialResponseDTO;
import erp.academico.modules.material.service.MaterialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/materiais")
@RequiredArgsConstructor
@Tag(name = "Materiais", description = "Materiais didáticos das disciplinas (PDF, vídeo, link, apresentação)")
public class MaterialController {

    private final MaterialService materialService;

    // --- BUSCA MATERIAL PELO ID ---
    @GetMapping("/{id}")
    @Operation(summary = "Busca material pelo ID")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR','ALUNO','RESPONSAVEL')")
    public ResponseEntity<MaterialResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(materialService.buscarPorId(id));
    }

    // --- CRIA MATERIAL ---
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Cria material (upload de arquivo ou link externo)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    public ResponseEntity<MaterialResponseDTO> criar(
            @Valid @RequestPart("dados") MaterialRequestDTO dto,
            @RequestPart(value = "arquivo", required = false) MultipartFile arquivo,
            UriComponentsBuilder uriBuilder) {
        MaterialResponseDTO criado = materialService.criar(dto, arquivo);
        URI uri = uriBuilder.path("/materiais/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    // --- ATUALIZA METADADOS ---
    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Atualiza material existente")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    public ResponseEntity<MaterialResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestPart("dados") MaterialRequestDTO dto,
            @RequestPart(value = "arquivo", required = false) MultipartFile arquivo) {
        return ResponseEntity.ok(materialService.atualizar(id, dto, arquivo));
    }

    // --- REMOVE O MATERIAL ---
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um material")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','PROFESSOR')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        materialService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- LISTA MATERIAIS DA TURMA + DISCIPLINA ---
    @GetMapping("/turmas/{turmaId}/disciplinas/{disciplinaId}")
    @Operation(summary = "Lista materiais por turma e disciplina")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR','ALUNO','RESPONSAVEL')")
    public ResponseEntity<Page<MaterialResponseDTO>> listarPorTurmaDisciplina(
            @PathVariable UUID turmaId,
            @PathVariable UUID disciplinaId,
            Pageable pageable) {
        return ResponseEntity.ok(materialService.listarPorTurmaDisciplina(turmaId, disciplinaId, pageable));
    }
}

