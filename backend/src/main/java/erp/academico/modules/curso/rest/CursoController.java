package erp.academico.modules.curso.rest;

import erp.academico.modules.curso.dto.CursoRequestDTO;
import erp.academico.modules.curso.dto.CursoResponseDTO;
import erp.academico.modules.curso.model.NivelCurso;
import erp.academico.modules.curso.service.CursoService;
import erp.academico.modules.disciplina.dto.DisciplinaResponseDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "CRUD de cursos oferecidos pela instituição")
public class CursoController {

    // --- DEPENDÊNCIAS INJETADAS VIA CONSTRUTOR ---
    private final CursoService cursoService;
    private final DisciplinaService disciplinaService;

    // --- LISTA PAGINADA COM FILTRO ---
    @GetMapping
    @Operation(summary = "Lista cursos paginados (opcionalmente filtrando por nível)")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<Page<CursoResponseDTO>> listar(
            @RequestParam(required = false) NivelCurso nivel,
            Pageable pageable) {
        return ResponseEntity.ok(cursoService.listar(nivel, pageable));
    }

    // --- BUSCA POR ID ---
    @GetMapping("/{id}")
    @Operation(summary = "Busca curso pelo ID")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<CursoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(cursoService.buscarPorId(id));
    }

    // --- CRIA UM NOVO CURSO ---
    @PostMapping
    @Operation(summary = "Cria um novo curso")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<CursoResponseDTO> criar(@Valid @RequestBody CursoRequestDTO dto,
                                                  UriComponentsBuilder uriBuilder) {
        CursoResponseDTO criado = cursoService.criar(dto);
        URI uri = uriBuilder.path("/cursos/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    // --- ATUALIZA UM CURSO EXISTENTE ---
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um curso existente")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR')")
    public ResponseEntity<CursoResponseDTO> atualizar(@PathVariable UUID id,
                                                      @Valid @RequestBody CursoRequestDTO dto) {
        return ResponseEntity.ok(cursoService.atualizar(id, dto));
    }

    // --- REMOVE O CURSO  ---
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um curso")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        cursoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- LISTA AS DISCIPLINAS DO CURSO ---
    @GetMapping("/{id}/disciplinas")
    @Operation(summary = "Lista as disciplinas do curso")
    @PreAuthorize("hasAnyRole('ADMIN','COORDENADOR','SECRETARIA','PROFESSOR')")
    public ResponseEntity<Page<DisciplinaResponseDTO>> listarDisciplinas(@PathVariable UUID id,
                                                                         Pageable pageable) {
        return ResponseEntity.ok(disciplinaService.listarPorCurso(id, pageable));
    }
}
