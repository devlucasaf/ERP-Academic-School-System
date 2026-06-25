package erp.academico.modules.material.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.infra.security.UsuarioDetails;
import erp.academico.infra.storage.StorageService;
import erp.academico.modules.material.dto.MaterialRequestDTO;
import erp.academico.modules.material.dto.MaterialResponseDTO;
import erp.academico.modules.material.model.Material;
import erp.academico.modules.material.model.TipoMaterial;
import erp.academico.modules.material.repository.MaterialRepository;
import erp.academico.modules.professor.model.Professor;
import erp.academico.modules.turma.model.TurmaDisciplina;
import erp.academico.modules.turma.repository.TurmaDisciplinaRepository;
import erp.academico.modules.usuario.model.RoleUsuario;
import erp.academico.modules.usuario.model.Usuario;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private static final String SUBDIR_MATERIAIS = "materiais";

    private final MaterialRepository materialRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final StorageService storageService;

    // --- LISTA OS MATERIAIS DE UMA TURMA + DISCIPLINA ---
    @Transactional(readOnly = true)
    public Page<MaterialResponseDTO> listarPorTurmaDisciplina(UUID turmaId, UUID disciplinaId, Pageable pageable) {
        return materialRepository.findByTurmaIdAndDisciplinaId(turmaId, disciplinaId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public MaterialResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    // --- CRIA UM MATERIAL PARA UMA TURMA/DISCIPLINA ---
    @Transactional
    public MaterialResponseDTO criar(MaterialRequestDTO dto, MultipartFile arquivo) {
        TurmaDisciplina td = buscarTurmaDisciplina(dto.getTurmaDisciplinaId());
        Usuario autenticado = usuarioAutenticadoOuFalha();
        Professor professor = validarProfessorDaDisciplina(td, autenticado);

        Material material = Material.builder()
                .turmaDisciplina(td)
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .tipo(dto.getTipo())
                .linkUrl(dto.getLinkUrl())
                .professor(professor)
                .build();

        aplicarRegraDeArquivoOuLink(material, dto, arquivo);

        return toResponse(materialRepository.save(material));
    }

    // --- ATUALIZA METADADOS DO MATERIAL ---
    @Transactional
    public MaterialResponseDTO atualizar(UUID id, MaterialRequestDTO dto, MultipartFile arquivo) {
        Material material = buscarEntidade(id);
        Usuario autenticado = usuarioAutenticadoOuFalha();
        validarPodeEditar(material, autenticado);

        if (!material.getTurmaDisciplina().getId().equals(dto.getTurmaDisciplinaId())) {
            material.setTurmaDisciplina(buscarTurmaDisciplina(dto.getTurmaDisciplinaId()));
        }

        material.setTitulo(dto.getTitulo());
        material.setDescricao(dto.getDescricao());
        material.setTipo(dto.getTipo());
        material.setLinkUrl(dto.getLinkUrl());

        aplicarRegraDeArquivoOuLink(material, dto, arquivo);

        return toResponse(materialRepository.save(material));
    }

    // --- REMOVE O MATERIAL ---
    @Transactional
    public void deletar(UUID id) {
        Material material = buscarEntidade(id);
        Usuario autenticado = usuarioAutenticadoOuFalha();
        validarPodeEditar(material, autenticado);

        if (material.getArquivoUrl() != null) {
            storageService.delete(material.getArquivoUrl());
        }
        materialRepository.delete(material);
    }

    // --- HELPERS ---

    private Material buscarEntidade(UUID id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material", id));
    }

    private TurmaDisciplina buscarTurmaDisciplina(UUID id) {
        return turmaDisciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo turma/disciplina", id));
    }

    // --- GARANTE COERÊNCIA ---
    private void aplicarRegraDeArquivoOuLink(Material material, MaterialRequestDTO dto, MultipartFile arquivo) {
        if (dto.getTipo() == TipoMaterial.LINK) {
            if (dto.getLinkUrl() == null || dto.getLinkUrl().isBlank()) {
                throw new BusinessException("Para tipo LINK é obrigatório informar 'linkUrl'.");
            }

            if (material.getArquivoUrl() != null) {
                storageService.delete(material.getArquivoUrl());
                material.setArquivoUrl(null);
            }
            return;
        }

        if (arquivo != null && !arquivo.isEmpty()) {
            if (material.getArquivoUrl() != null) {
                storageService.delete(material.getArquivoUrl());
            }
            material.setArquivoUrl(storageService.store(arquivo, SUBDIR_MATERIAIS));
            material.setLinkUrl(null);
        } else if (material.getArquivoUrl() == null) {
            throw new BusinessException("Para o tipo " + dto.getTipo() + " é obrigatório enviar um arquivo.");
        }
    }

    private Usuario usuarioAutenticadoOuFalha() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioDetails ud)) {
            throw new BusinessException("Usuário autenticado não identificado.");
        }
        return ud.getUsuario();
    }

    private Professor validarProfessorDaDisciplina(TurmaDisciplina td, Usuario autenticado) {
        Professor professor = td.getProfessor();
        if (professor == null || professor.getUsuario() == null) {
            throw new BusinessException("Vínculo turma/disciplina não possui professor responsável.");
        }

        if (autenticado.getRole() == RoleUsuario.ADMIN
                || autenticado.getRole() == RoleUsuario.COORDENADOR) {
            return professor;
        }

        if (!professor.getUsuario().getId().equals(autenticado.getId())) {
            throw new BusinessException("Apenas o professor responsável pela disciplina pode realizar esta operação.");
        }
        return professor;
    }

    private void validarPodeEditar(Material material, Usuario autenticado) {
        if (autenticado.getRole() == RoleUsuario.ADMIN
                || autenticado.getRole() == RoleUsuario.COORDENADOR) {
            return;
        }

        if (!material.getProfessor().getUsuario().getId().equals(autenticado.getId())) {
            throw new BusinessException("Apenas o professor responsável pode editar este material.");
        }
    }

    private MaterialResponseDTO toResponse(Material m) {
        return MaterialResponseDTO.builder()
                .id(m.getId())
                .turmaDisciplinaId(m.getTurmaDisciplina().getId())
                .titulo(m.getTitulo())
                .descricao(m.getDescricao())
                .tipo(m.getTipo())
                .arquivoUrl(m.getArquivoUrl())
                .linkUrl(m.getLinkUrl())
                .professorId(m.getProfessor().getId())
                .professorNome(m.getProfessor().getUsuario() == null ? null : m.getProfessor().getUsuario().getNome())
                .criadoEm(m.getCriadoEm())
                .atualizadoEm(m.getAtualizadoEm())
                .build();
    }
}

