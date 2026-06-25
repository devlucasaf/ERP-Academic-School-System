package erp.academico.modules.funcionario.service;

import erp.academico.exception.ResourceNotFoundException;
import erp.academico.modules.funcionario.dto.FuncionarioRequestDTO;
import erp.academico.modules.funcionario.dto.FuncionarioResponseDTO;
import erp.academico.modules.funcionario.model.CargoFuncionario;
import erp.academico.modules.funcionario.model.Funcionario;
import erp.academico.modules.funcionario.repository.FuncionarioRepository;
import erp.academico.modules.usuario.dto.UsuarioRequestDTO;
import erp.academico.modules.usuario.dto.UsuarioResponseDTO;
import erp.academico.modules.usuario.model.Usuario;
import erp.academico.modules.usuario.service.UsuarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioService usuarioService;

    // --- LISTA FUNCIONÁRIOS ---
    @Transactional(readOnly = true)
    public Page<FuncionarioResponseDTO> listar(CargoFuncionario cargo, Pageable pageable) {
        Page<Funcionario> page = (cargo == null)
                ? funcionarioRepository.findAll(pageable)
                : funcionarioRepository.findByCargo(cargo, pageable);
        return page.map(this::toResponse);
    }

    // --- BUSCA FUNCIONÁRIO POR ID ---
    @Transactional(readOnly = true)
    public FuncionarioResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    // --- CRIA UM NOVO FUNCIONÁRIO JUNTO COM O USUÁRIO CORRESPONDENTE ---
    @Transactional
    public FuncionarioResponseDTO criar(FuncionarioRequestDTO dto) {
        Usuario usuario = usuarioService.criarEntidade(UsuarioRequestDTO.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .cpf(dto.getCpf())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .ativo(true)
                .role(dto.getCargo().toRoleUsuario())
                .build());

        Funcionario funcionario = Funcionario.builder()
                .usuario(usuario)
                .cargo(dto.getCargo())
                .dataAdmissao(dto.getDataAdmissao())
                .departamento(dto.getDepartamento())
                .build();

        return toResponse(funcionarioRepository.save(funcionario));
    }

    // --- ATUALIZA OS DADOS DO FUNCIONÁRIO E DO USUÁRIO VINCULADO ---
    @Transactional
    public FuncionarioResponseDTO atualizar(UUID id, FuncionarioRequestDTO dto) {
        Funcionario funcionario = buscarEntidade(id);
        Usuario usuario = funcionario.getUsuario();

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf());
        usuario.setTelefone(dto.getTelefone());
        usuario.setDataNascimento(dto.getDataNascimento());

        if (!funcionario.getCargo().equals(dto.getCargo())) {
            usuario.setRole(dto.getCargo().toRoleUsuario());
            funcionario.setCargo(dto.getCargo());
        }

        funcionario.setDataAdmissao(dto.getDataAdmissao());
        funcionario.setDepartamento(dto.getDepartamento());

        return toResponse(funcionarioRepository.save(funcionario));
    }

    // --- REMOVE UM FUNCIONÁRIO ---
    @Transactional
    public void deletar(UUID id) {
        Funcionario funcionario = buscarEntidade(id);
        funcionarioRepository.delete(funcionario);
    }

    // --- BUSCA A ENTIDADE ---
    private Funcionario buscarEntidade(UUID id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário", id));
    }

    // --- CONVERTE Funcionario PARA O DTO DE RESPOSTA ---
    private FuncionarioResponseDTO toResponse(Funcionario funcionario) {
        Usuario u = funcionario.getUsuario();
        UsuarioResponseDTO usuarioDto = UsuarioResponseDTO.builder()
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .cpf(u.getCpf())
                .telefone(u.getTelefone())
                .dataNascimento(u.getDataNascimento())
                .ativo(u.getAtivo())
                .role(u.getRole())
                .criadoEm(u.getCriadoEm())
                .atualizadoEm(u.getAtualizadoEm())
                .build();

        return FuncionarioResponseDTO.builder()
                .id(funcionario.getId())
                .usuario(usuarioDto)
                .cargo(funcionario.getCargo())
                .dataAdmissao(funcionario.getDataAdmissao())
                .departamento(funcionario.getDepartamento())
                .criadoEm(funcionario.getCriadoEm())
                .atualizadoEm(funcionario.getAtualizadoEm())
                .build();
    }
}
