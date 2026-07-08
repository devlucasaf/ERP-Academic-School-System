package erp.academico.modules.usuario.service;

// --- IMPORTS ---
import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.infra.email.EmailService;
import erp.academico.infra.security.GeradorSenhaTemporaria;
import erp.academico.modules.usuario.dto.UsuarioRequestDTO;
import erp.academico.modules.usuario.dto.UsuarioResponseDTO;
import erp.academico.modules.usuario.model.Usuario;
import erp.academico.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeradorSenhaTemporaria geradorSenhaTemporaria;
    private final EmailService emailService;

    // --- LISTA USUÁRIOS PAGINADOS ---
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::toResponse);
    }

    // --- BUSCA USUÁRIO POR ID ---
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    // --- CRIA UM NOVO USUÁRIO ---
    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        return toResponse(criarEntidade(dto));
    }

    // --- CRIA UM NOVO USUÁRIO E RETORNA A ENTIDADE ---
    @Transactional
    public Usuario criarEntidade(UsuarioRequestDTO dto) {
        // --- GARANTE UNICIDADE DO E-MAIL ---
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Já existe um usuário cadastrado com o e-mail: " + dto.getEmail());
        }

        // --- SE A SENHA NÃO FOI INFORMADA, GERA UMA SENHA TEMPORÁRIA ---
        boolean senhaGerada = dto.getSenha() == null || dto.getSenha().isBlank();
        String senhaFinal = senhaGerada ? geradorSenhaTemporaria.gerar() : dto.getSenha();

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(senhaFinal))
                .cpf(dto.getCpf())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .ativo(dto.getAtivo() == null ? Boolean.TRUE : dto.getAtivo())
                .role(dto.getRole())
                .build();

        Usuario salvo = usuarioRepository.save(usuario);

        // --- "ENVIA" A SENHA TEMPORÁRIA POR E-MAIL (APENAS LOG POR ENQUANTO) ---
        if (senhaGerada) {
            emailService.enviarSenhaTemporaria(salvo.getEmail(), salvo.getNome(), senhaFinal);
        }

        return salvo;
    }

    // --- ATUALIZA UM USUÁRIO EXISTENTE ---
    @Transactional
    public UsuarioResponseDTO atualizar(UUID id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarEntidade(id);

        if (!usuario.getEmail().equalsIgnoreCase(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Já existe um usuário cadastrado com o e-mail: " + dto.getEmail());
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf());
        usuario.setTelefone(dto.getTelefone());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setRole(dto.getRole());

        if (dto.getAtivo() != null) {
            usuario.setAtivo(dto.getAtivo());
        }

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    // --- REMOVE UM USUÁRIO ---
    @Transactional
    public void deletar(UUID id) {
        Usuario usuario = buscarEntidade(id);
        usuarioRepository.delete(usuario);
    }

    // --- BUSCA A ENTIDADE ---
    private Usuario buscarEntidade(UUID id) {
        return usuarioRepository.findById(id) .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    // --- CONVERTE A ENTIDADE Usuario PARA O DTO DE RESPOSTA ---
    private UsuarioResponseDTO toResponse(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .telefone(usuario.getTelefone())
                .dataNascimento(usuario.getDataNascimento())
                .ativo(usuario.getAtivo())
                .role(usuario.getRole())
                .criadoEm(usuario.getCriadoEm())
                .atualizadoEm(usuario.getAtualizadoEm())
                .build();
    }
}
