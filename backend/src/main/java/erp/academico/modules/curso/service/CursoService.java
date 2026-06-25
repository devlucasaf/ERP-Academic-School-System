package erp.academico.modules.curso.service;

import erp.academico.exception.ResourceNotFoundException;
import erp.academico.modules.curso.dto.CursoRequestDTO;
import erp.academico.modules.curso.dto.CursoResponseDTO;
import erp.academico.modules.curso.model.Curso;
import erp.academico.modules.curso.model.NivelCurso;
import erp.academico.modules.curso.repository.CursoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;

    // --- LISTA CURSOS ---
    @Transactional(readOnly = true)
    public Page<CursoResponseDTO> listar(NivelCurso nivel, Pageable pageable) {
        Page<Curso> page = (nivel == null)
                ? cursoRepository.findAll(pageable)
                : cursoRepository.findByNivel(nivel, pageable);
        return page.map(this::toResponse);
    }

    // --- BUSCA CURSO POR ID ---
    @Transactional(readOnly = true)
    public CursoResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidade(id));
    }

    // --- EXPÕE A ENTIDADE PARA OUTROS SERVICES ---
    @Transactional(readOnly = true)
    public Curso buscarEntidade(UUID id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", id));
    }

    // --- CRIA UM NOVO CURSO ---
    @Transactional
    public CursoResponseDTO criar(CursoRequestDTO dto) {
        Curso curso = Curso.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .nivel(dto.getNivel())
                .duracaoSemestres(dto.getDuracaoSemestres())
                .cargaHorariaTotal(dto.getCargaHorariaTotal())
                .ativo(dto.getAtivo() == null ? Boolean.TRUE : dto.getAtivo())
                .build();
        return toResponse(cursoRepository.save(curso));
    }

    // --- ATUALIZA OS DADOS DE UM CURSO EXISTENTE ---
    @Transactional
    public CursoResponseDTO atualizar(UUID id, CursoRequestDTO dto) {
        Curso curso = buscarEntidade(id);
        curso.setNome(dto.getNome());
        curso.setDescricao(dto.getDescricao());
        curso.setNivel(dto.getNivel());
        curso.setDuracaoSemestres(dto.getDuracaoSemestres());
        curso.setCargaHorariaTotal(dto.getCargaHorariaTotal());

        if (dto.getAtivo() != null) {
            curso.setAtivo(dto.getAtivo());
        }
        return toResponse(cursoRepository.save(curso));
    }

    // --- REMOVE UM CURSO ---
    @Transactional
    public void deletar(UUID id) {
        Curso curso = buscarEntidade(id);
        cursoRepository.delete(curso);
    }

    // --- CONVERTE A ENTIDADE Curso PARA O DTO DE RESPOSTA ---
    private CursoResponseDTO toResponse(Curso curso) {
        return CursoResponseDTO.builder()
                .id(curso.getId())
                .nome(curso.getNome())
                .descricao(curso.getDescricao())
                .nivel(curso.getNivel())
                .duracaoSemestres(curso.getDuracaoSemestres())
                .cargaHorariaTotal(curso.getCargaHorariaTotal())
                .ativo(curso.getAtivo())
                .criadoEm(curso.getCriadoEm())
                .atualizadoEm(curso.getAtualizadoEm())
                .build();
    }
}
