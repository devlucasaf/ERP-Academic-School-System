package erp.academico.modules.autenticacao.rest;

import erp.academico.dto.auth.LoginRequestDTO;
import erp.academico.dto.auth.LoginResponseDTO;
import erp.academico.dto.auth.RefreshTokenRequestDTO;
import erp.academico.dto.auth.RegisterRequestDTO;
import erp.academico.modules.autenticacao.service.AutenticacaoService;
import erp.academico.modules.usuario.dto.UsuarioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login, registro e refresh de tokens JWT")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    @PostMapping("/login")
    @Operation(summary = "Autentica o usuário e retorna o token JWT + refresh token")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(autenticacaoService.login(dto));
    }

    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuário no sistema")
    public ResponseEntity<UsuarioResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(201).body(autenticacaoService.register(dto));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Gera um novo access token a partir de um refresh token válido")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        return ResponseEntity.ok(autenticacaoService.refresh(dto));
    }
}

