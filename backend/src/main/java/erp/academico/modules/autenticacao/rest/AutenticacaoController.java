package erp.academico.modules.autenticacao.rest;

import erp.academico.dto.auth.LoginRequestDTO;
import erp.academico.dto.auth.LoginResponseDTO;
import erp.academico.dto.auth.RefreshTokenRequestDTO;
import erp.academico.dto.auth.RegisterRequestDTO;
import erp.academico.infra.security.UsuarioDetails;
import erp.academico.modules.autenticacao.service.AutenticacaoService;
import erp.academico.modules.usuario.dto.UsuarioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

    // --- AUTENTICA O USUÁRIO ---
    @PostMapping("/login")
    @Operation(summary = "Autentica o usuário e retorna o token JWT + refresh token")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(autenticacaoService.login(dto));
    }

    // --- SOMENTE ADMIN PODE REGISTRAR NOVOS USUÁRIOS ---
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Registra um novo usuário no sistema (apenas ADMIN)")
    public ResponseEntity<UsuarioResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(201).body(autenticacaoService.register(dto));
    }

    // --- RENOVA O ACCESS TOKEN A PARTIR DO REFRESH TOKEN ---
    @PostMapping("/refresh")
    @Operation(summary = "Gera um novo access token a partir de um refresh token válido")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        return ResponseEntity.ok(autenticacaoService.refresh(dto));
    }

    // --- RETORNA OS DADOS DO USUÁRIO AUTENTICADO ---
    @GetMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Retorna os dados do usuário atualmente autenticado")
    public ResponseEntity<UsuarioResponseDTO> me(@AuthenticationPrincipal UsuarioDetails usuarioDetails) {
        return ResponseEntity.ok(autenticacaoService.dadosDoUsuario(usuarioDetails.getUsuario().getId()));
    }
}

