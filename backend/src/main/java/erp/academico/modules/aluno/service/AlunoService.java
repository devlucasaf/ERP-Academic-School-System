package erp.academico.modules.aluno.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.modules.aluno.dto.AlunoRequestDTO;
import erp.academico.modules.aluno.dto.AlunoResponseDTO;
import erp.academico.modules.aluno.model.Aluno;
import erp.academico.modules.aluno.model.StatusAluno;
import erp.academico.modules.aluno.repository.AlunoRepository;
import erp.academico.modules.usuario.dto.UsuarioRequestDTO;
import erp.academico.modules.usuario.dto.UsuarioResponseDTO;
import erp.academico.modules.usuario.model.RoleUsuario;
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
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public Page<AlunoResponseDTO> listar(StatusAluno status, Pageable pageable) {
        Page<Aluno> page = (status == null)
                ? alunoRepository.findAll(pageable)
                : alunoRepository.findByStatus(status, pageable);
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AlunoResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public AlunoResponseDTO buscarPorMatricula(String matriculaRA) {
        Aluno aluno = alunoRepository.findByMatriculaRA(matriculaRA)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno (matrícula)", matriculaRA));
        return toResponse(aluno);
    }

    @Transactional
    public AlunoResponseDTO criar(AlunoRequestDTO dto) {
        if (alunoRepository.existsByMatriculaRA(dto.getMatriculaRA())) {
            throw new BusinessException("Já existe um aluno cadastrado com a matrícula: " + dto.getMatriculaRA());
        }

        Usuario usuario = usuarioService.criarEntidade(UsuarioRequestDTO.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .cpf(dto.getCpf())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .ativo(true)
                .role(RoleUsuario.ALUNO)
                .build());

        Aluno aluno = Aluno.builder()
                .usuario(usuario)
                .matriculaRA(dto.getMatriculaRA())
                .dataIngresso(dto.getDataIngresso())
                .status(dto.getStatus())
                .turmaAtualId(dto.getTurmaAtualId())
                .observacoes(dto.getObservacoes())
                .build();

        return toResponse(alunoRepository.save(aluno));
    }

    @Transactional
    public AlunoResponseDTO atualizar(UUID id, AlunoRequestDTO dto) {
        Aluno aluno = buscarEntidade(id);

        if (!aluno.getMatriculaRA().equalsIgnoreCase(dto.getMatriculaRA()) && alunoRepository.existsByMatriculaRA(dto.getMatriculaRA())) {
            throw new BusinessException("Já existe um aluno com a matrícula: " + dto.getMatriculaRA());
        }

        // --- ATUALIZA DADOS DO USUÁRIO VINCULADO ---
        Usuario usuario = aluno.getUsuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf());
        usuario.setTelefone(dto.getTelefone());
        usuario.setDataNascimento(dto.getDataNascimento());

        // --- ATUALIZA DADOS ACADÊMICOS ---
        aluno.setMatriculaRA(dto.getMatriculaRA());
        aluno.setDataIngresso(dto.getDataIngresso());
        aluno.setStatus(dto.getStatus());
        aluno.setTurmaAtualId(dto.getTurmaAtualId());
        aluno.setObservacoes(dto.getObservacoes());

        return toResponse(alunoRepository.save(aluno));
    }

    @Transactional
    public void deletar(UUID id) {
        Aluno aluno = buscarEntidade(id);
        alunoRepository.delete(aluno);
    }

    private Aluno buscarEntidade(UUID id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }

    private AlunoResponseDTO toResponse(Aluno aluno) {
        Usuario u = aluno.getUsuario();
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

        return AlunoResponseDTO.builder()
                .id(aluno.getId())
                .usuario(usuarioDto)
                .matriculaRA(aluno.getMatriculaRA())
                .dataIngresso(aluno.getDataIngresso())
                .status(aluno.getStatus())
                .turmaAtualId(aluno.getTurmaAtualId())
                .observacoes(aluno.getObservacoes())
                .criadoEm(aluno.getCriadoEm())
                .atualizadoEm(aluno.getAtualizadoEm())
                .build();
    }
}

