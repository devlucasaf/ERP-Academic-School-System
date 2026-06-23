package erp.academico.modules.autenticacao.service;

import erp.academico.dto.auth.LoginRequestDTO;
import erp.academico.dto.auth.LoginResponseDTO;
import erp.academico.dto.auth.RefreshTokenRequestDTO;
import erp.academico.dto.auth.RegisterRequestDTO;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;

import erp.academico.infra.security.TokenService;
import erp.academico.infra.security.UsuarioDetails;

import erp.academico.modules.usuario.dto.UsuarioRequestDTO;
import erp.academico.modules.usuario.dto.UsuarioResponseDTO;
import erp.academico.modules.usuario.model.Usuario;
import erp.academico.modules.usuario.repository.UsuarioRepository;
import erp.academico.modules.usuario.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );

            Usuario usuario = ((UsuarioDetails) auth.getPrincipal()).getUsuario();
            return montarLoginResponse(usuario);
        } catch (BadCredentialsException ex) {
            throw new BusinessException("E-mail ou senha inválidos.");
        }
    }

    @Transactional
    public UsuarioResponseDTO register(RegisterRequestDTO dto) {
        UsuarioRequestDTO request = UsuarioRequestDTO.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .cpf(dto.getCpf())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .ativo(true)
                .role(dto.getRole())
                .build();

        return usuarioService.criar(request);
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO refresh(RefreshTokenRequestDTO dto) {
        String email = tokenService.validarRefreshToken(dto.getRefreshToken());

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", email));

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new BusinessException("Usuário inativo.");
        }

        return montarLoginResponse(usuario);
    }

    private LoginResponseDTO montarLoginResponse(Usuario usuario) {
        return LoginResponseDTO.builder()
                .token(tokenService.gerarToken(usuario))
                .refreshToken(tokenService.gerarRefreshToken(usuario))
                .usuario(usuarioService.buscarPorId(usuario.getId()))
                .build();
    }
}

