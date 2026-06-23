package erp.academico.modules.responsavel.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.modules.aluno.model.Aluno;
import erp.academico.modules.aluno.repository.AlunoRepository;
import erp.academico.modules.responsavel.dto.ResponsavelAlunoResponseDTO;
import erp.academico.modules.responsavel.dto.ResponsavelRequestDTO;
import erp.academico.modules.responsavel.dto.ResponsavelResponseDTO;
import erp.academico.modules.responsavel.dto.VincularAlunoRequestDTO;
import erp.academico.modules.responsavel.model.Responsavel;
import erp.academico.modules.responsavel.model.ResponsavelAluno;
import erp.academico.modules.responsavel.repository.ResponsavelAlunoRepository;
import erp.academico.modules.responsavel.repository.ResponsavelRepository;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;
    private final ResponsavelAlunoRepository responsavelAlunoRepository;
    private final AlunoRepository alunoRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public Page<ResponsavelResponseDTO> listar(Pageable pageable) {
        return responsavelRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ResponsavelResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public ResponsavelResponseDTO criar(ResponsavelRequestDTO dto) {
        Usuario usuario = usuarioService.criarEntidade(UsuarioRequestDTO.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .cpf(dto.getCpf())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .ativo(true)
                .role(RoleUsuario.RESPONSAVEL)
                .build());

        Responsavel responsavel = Responsavel.builder()
                .usuario(usuario)
                .parentesco(dto.getParentesco())
                .build();

        return toResponse(responsavelRepository.save(responsavel));
    }

    @Transactional
    public ResponsavelResponseDTO atualizar(UUID id, ResponsavelRequestDTO dto) {
        Responsavel responsavel = buscarEntidade(id);

        Usuario usuario = responsavel.getUsuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf());
        usuario.setTelefone(dto.getTelefone());
        usuario.setDataNascimento(dto.getDataNascimento());

        responsavel.setParentesco(dto.getParentesco());

        return toResponse(responsavelRepository.save(responsavel));
    }

    @Transactional
    public void deletar(UUID id) {
        Responsavel responsavel = buscarEntidade(id);
        responsavelRepository.delete(responsavel);
    }

    // --- VÍNCULOS COM ALUNOS ---

    @Transactional(readOnly = true)
    public List<ResponsavelAlunoResponseDTO> listarAlunos(UUID responsavelId) {
        buscarEntidade(responsavelId); // valida existência
        return responsavelAlunoRepository.findByResponsavelId(responsavelId)
                .stream()
                .map(this::toVinculoResponse)
                .toList();
    }

    @Transactional
    public ResponsavelAlunoResponseDTO vincularAluno(UUID responsavelId, VincularAlunoRequestDTO dto) {
        Responsavel responsavel = buscarEntidade(responsavelId);
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", dto.getAlunoId()));

        if (responsavelAlunoRepository.existsByResponsavelIdAndAlunoId(responsavelId, aluno.getId())) {
            throw new BusinessException("Este aluno já está vinculado ao responsável.");
        }

        ResponsavelAluno vinculo = ResponsavelAluno.builder()
                .responsavel(responsavel)
                .aluno(aluno)
                .observacao(dto.getObservacao())
                .build();

        return toVinculoResponse(responsavelAlunoRepository.save(vinculo));
    }

    @Transactional
    public void desvincularAluno(UUID responsavelId, UUID alunoId) {
        ResponsavelAluno vinculo = responsavelAlunoRepository
                .findByResponsavelIdAndAlunoId(responsavelId, alunoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vínculo responsável/aluno", responsavelId + "/" + alunoId));
        responsavelAlunoRepository.delete(vinculo);
    }

    // --- HELPERS ---

    private Responsavel buscarEntidade(UUID id) {
        return responsavelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável", id));
    }

    private ResponsavelResponseDTO toResponse(Responsavel responsavel) {
        Usuario u = responsavel.getUsuario();
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

        return ResponsavelResponseDTO.builder()
                .id(responsavel.getId())
                .usuario(usuarioDto)
                .parentesco(responsavel.getParentesco())
                .criadoEm(responsavel.getCriadoEm())
                .atualizadoEm(responsavel.getAtualizadoEm())
                .build();
    }

    private ResponsavelAlunoResponseDTO toVinculoResponse(ResponsavelAluno v) {
        Aluno a = v.getAluno();
        return ResponsavelAlunoResponseDTO.builder()
                .id(v.getId())
                .responsavelId(v.getResponsavel().getId())
                .alunoId(a.getId())
                .alunoNome(a.getUsuario().getNome())
                .alunoMatriculaRA(a.getMatriculaRA())
                .observacao(v.getObservacao())
                .criadoEm(v.getCriadoEm())
                .build();
    }
}

