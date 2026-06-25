package erp.academico.modules.professor.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.modules.professor.dto.ProfessorDisciplinaResponseDTO;
import erp.academico.modules.professor.dto.ProfessorRequestDTO;
import erp.academico.modules.professor.dto.ProfessorResponseDTO;
import erp.academico.modules.professor.dto.VincularDisciplinaRequestDTO;
import erp.academico.modules.professor.model.Professor;
import erp.academico.modules.professor.model.ProfessorDisciplina;
import erp.academico.modules.professor.repository.ProfessorDisciplinaRepository;
import erp.academico.modules.professor.repository.ProfessorRepository;
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
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final ProfessorDisciplinaRepository professorDisciplinaRepository;
    private final UsuarioService usuarioService;

    // --- LISTA PROFESSORES PAGINADOS ---
    @Transactional(readOnly = true)
    public Page<ProfessorResponseDTO> listar(Pageable pageable) {
        return professorRepository.findAll(pageable).map(this::toResponse);
    }

    // --- BUSCA PROFESSOR POR ID ---
    @Transactional(readOnly = true)
    public ProfessorResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    // --- CRIA UM NOVO PROFESSOR JUNTO COM O USUÁRIO CORRESPONDENTE ---
    @Transactional
    public ProfessorResponseDTO criar(ProfessorRequestDTO dto) {
        Usuario usuario = usuarioService.criarEntidade(UsuarioRequestDTO.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .cpf(dto.getCpf())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .ativo(true)
                .role(RoleUsuario.PROFESSOR)
                .build());

        Professor professor = Professor.builder()
                .usuario(usuario)
                .formacao(dto.getFormacao())
                .areaAtuacao(dto.getAreaAtuacao())
                .cargaHorariaSemanal(dto.getCargaHorariaSemanal())
                .dataAdmissao(dto.getDataAdmissao())
                .ativo(dto.getAtivo() == null ? Boolean.TRUE : dto.getAtivo())
                .build();

        return toResponse(professorRepository.save(professor));
    }

    // --- ATUALIZA OS DADOS DO PROFESSOR E DO USUÁRIO VINCULADO ---
    @Transactional
    public ProfessorResponseDTO atualizar(UUID id, ProfessorRequestDTO dto) {
        Professor professor = buscarEntidade(id);
        Usuario usuario = professor.getUsuario();

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf());
        usuario.setTelefone(dto.getTelefone());
        usuario.setDataNascimento(dto.getDataNascimento());

        professor.setFormacao(dto.getFormacao());
        professor.setAreaAtuacao(dto.getAreaAtuacao());
        professor.setCargaHorariaSemanal(dto.getCargaHorariaSemanal());
        professor.setDataAdmissao(dto.getDataAdmissao());

        if (dto.getAtivo() != null) {
            professor.setAtivo(dto.getAtivo());
        }

        return toResponse(professorRepository.save(professor));
    }

    // --- REMOVE O PROFESSOR ---
    @Transactional
    public void deletar(UUID id) {
        Professor professor = buscarEntidade(id);
        professorRepository.delete(professor);
    }

    // --- LISTA AS DISCIPLINAS VINCULADAS AO PROFESSOR ---
    @Transactional(readOnly = true)
    public List<ProfessorDisciplinaResponseDTO> listarDisciplinas(UUID professorId) {
        buscarEntidade(professorId);
        return professorDisciplinaRepository.findByProfessorId(professorId)
                .stream()
                .map(this::toVinculoResponse)
                .toList();
    }

    // --- VINCULA UMA DISCIPLINA AO PROFESSOR ---
    @Transactional
    public ProfessorDisciplinaResponseDTO vincularDisciplina(UUID professorId, VincularDisciplinaRequestDTO dto) {
        Professor professor = buscarEntidade(professorId);

        if (professorDisciplinaRepository.existsByProfessorIdAndDisciplinaId(professorId, dto.getDisciplinaId())) {
            throw new BusinessException("Esta disciplina já está vinculada ao professor.");
        }

        ProfessorDisciplina vinculo = ProfessorDisciplina.builder()
                .professor(professor)
                .disciplinaId(dto.getDisciplinaId())
                .build();

        return toVinculoResponse(professorDisciplinaRepository.save(vinculo));
    }

    // --- REMOVE O VÍNCULO ENTRE PROFESSOR E DISCIPLINA ---
    @Transactional
    public void desvincularDisciplina(UUID professorId, UUID disciplinaId) {
        ProfessorDisciplina vinculo = professorDisciplinaRepository
                .findByProfessorIdAndDisciplinaId(professorId, disciplinaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vínculo professor/disciplina", professorId + "/" + disciplinaId));
        professorDisciplinaRepository.delete(vinculo);
    }

    // --- BUSCA A ENTIDADE ---
    private Professor buscarEntidade(UUID id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", id));
    }

    // --- CONVERTE Professor PARA O DTO DE RESPOSTA ---
    private ProfessorResponseDTO toResponse(Professor professor) {
        Usuario u = professor.getUsuario();
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

        return ProfessorResponseDTO.builder()
                .id(professor.getId())
                .usuario(usuarioDto)
                .formacao(professor.getFormacao())
                .areaAtuacao(professor.getAreaAtuacao())
                .cargaHorariaSemanal(professor.getCargaHorariaSemanal())
                .dataAdmissao(professor.getDataAdmissao())
                .ativo(professor.getAtivo())
                .criadoEm(professor.getCriadoEm())
                .atualizadoEm(professor.getAtualizadoEm())
                .build();
    }

    // --- CONVERTE O VÍNCULO PROFESSOR/DISCIPLINA PARA O DTO DE RESPOSTA ---
    private ProfessorDisciplinaResponseDTO toVinculoResponse(ProfessorDisciplina v) {
        return ProfessorDisciplinaResponseDTO.builder()
                .id(v.getId())
                .professorId(v.getProfessor().getId())
                .disciplinaId(v.getDisciplinaId())
                .criadoEm(v.getCriadoEm())
                .build();
    }
}
