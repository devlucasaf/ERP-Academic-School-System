package erp.academico.modules.biblioteca.configuracao.rest;

import erp.academico.modules.biblioteca.configuracao.dto.ConfiguracaoBibliotecaDTO;
import erp.academico.modules.biblioteca.configuracao.model.ConfiguracaoBiblioteca;
import erp.academico.modules.biblioteca.configuracao.service.ConfiguracaoBibliotecaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/biblioteca/configuracoes")
@RequiredArgsConstructor
@Tag(name = "Biblioteca - Configurações", description = "Prazos, limites e valor da multa")
public class ConfiguracaoBibliotecaController {

    private final ConfiguracaoBibliotecaService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('BIBLIOTECARIO','ADMIN')")
    public ResponseEntity<ConfiguracaoBiblioteca> obter() {
        return ResponseEntity.ok(service.obter());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','BIBLIOTECARIO')")
    public ResponseEntity<ConfiguracaoBiblioteca> atualizar(@Valid @RequestBody ConfiguracaoBibliotecaDTO dto) {
        return ResponseEntity.ok(service.atualizar(dto));
    }
}

