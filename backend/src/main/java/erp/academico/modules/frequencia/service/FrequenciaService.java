package erp.academico.modules.frequencia.service;

import erp.academico.exception.BusinessException;
import erp.academico.exception.ResourceNotFoundException;
import erp.academico.modules.aluno.model.Aluno;
import erp.academico.modules.frequencia.dto.AulaRequestDTO;
import erp.academico.modules.frequencia.dto.AulaResponseDTO;
import erp.academico.modules.frequencia.dto.ChamadaRequestDTO;
import erp.academico.modules.frequencia.dto.FrequenciaPercentualDTO;
import erp.academico.modules.frequencia.dto.FrequenciaResponseDTO;
import erp.academico.modules.frequencia.dto.FrequenciaResumoDTO;
import erp.academico.modules.frequencia.model.Aula;
import erp.academico.modules.frequencia.model.Frequencia;
import erp.academico.modules.frequencia.repository.AulaRepository;
import erp.academico.modules.frequencia.repository.FrequenciaRepository;
import erp.academico.modules.matricula.model.Matricula;
import erp.academico.modules.matricula.model.StatusMatricula;
import erp.academico.modules.matricula.repository.MatriculaRepository;
import erp.academico.modules.professor.model.Professor;
import erp.academico.modules.professor.repository.ProfessorRepository;
import erp.academico.modules.turma.model.TurmaDisciplina;
import erp.academico.modules.turma.repository.TurmaDisciplinaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FrequenciaService {

    private final AulaRepository aulaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final TurmaDisciplinaRepository turmaDisciplinaRepository;
    private final MatriculaRepository matriculaRepository;
    private final ProfessorRepository professorRepository;

    // --- CRIA UMA AULA E AUTOMATICAMENTE GERA REGISTROS DE FREQUÊNCIA PARA TODOS OS ALUNOS ATIVOS DA TURMA ---
    @Transactional
    public AulaResponseDTO criarAula(AulaRequestDTO dto) {
        TurmaDisciplina td = turmaDisciplinaRepository.findById(dto.getTurmaDisciplinaId())
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo turma/disciplina", dto.getTurmaDisciplinaId()));

        Professor professor;
        if (dto.getProfessorId() != null) {
            professor = professorRepository.findById(dto.getProfessorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Professor", dto.getProfessorId()));
        } else {
            professor = td.getProfessor();
        }

        Aula aula = Aula.builder()
                .turmaDisciplina(td)
                .dataAula(dto.getDataAula())
                .conteudoMinistrado(dto.getConteudoMinistrado())
                .professor(professor)
                .build();
        aula = aulaRepository.save(aula);

        List<Matricula> matriculasAtivas = matriculaRepository.findByTurmaIdAndStatus(
                td.getTurma().getId(), StatusMatricula.ATIVA);

        List<Frequencia> freqs = new ArrayList<>();
        for (Matricula m : matriculasAtivas) {
            freqs.add(Frequencia.builder()
                    .aula(aula)
                    .aluno(m.getAluno())
                    .presente(Boolean.TRUE)
                    .build());
        }
        frequenciaRepository.saveAll(freqs);

        return toAulaResponse(aula, freqs.size());
    }

    // --- REGISTRA AS PRESENÇAS/FALTAS DE UMA AULA EM LOTE ---
    @Transactional
    public List<FrequenciaResponseDTO> registrarChamada(UUID aulaId, ChamadaRequestDTO dto) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new ResourceNotFoundException("Aula", aulaId));

        List<FrequenciaResponseDTO> resultado = new ArrayList<>();
        for (ChamadaRequestDTO.ItemChamada item : dto.getRegistros()) {
            Frequencia f = frequenciaRepository
                    .findByAulaIdAndAlunoId(aula.getId(), item.getAlunoId())
                    .orElseThrow(() -> new BusinessException(
                            "Aluno " + item.getAlunoId() + " não possui registro de frequência nesta aula."));

            f.setPresente(item.getPresente());
            f.setJustificativa(Boolean.TRUE.equals(item.getPresente()) ? null : item.getJustificativa());

            resultado.add(toFrequenciaResponse(frequenciaRepository.save(f)));
        }
        return resultado;
    }

    // --- LISTA OS REGISTROS DE FREQUÊNCIA DE UMA AULA ---
    @Transactional(readOnly = true)
    public List<FrequenciaResponseDTO> listarFrequenciasDaAula(UUID aulaId) {
        aulaRepository.findById(aulaId)
                .orElseThrow(() -> new ResourceNotFoundException("Aula", aulaId));
        return frequenciaRepository.findByAulaId(aulaId)
                .stream()
                .map(this::toFrequenciaResponse)
                .toList();
    }

    // --- CALCULA O PERCENTUAL DE PRESENÇA DE UM ALUNO EM UMA DISCIPLINA ESPECÍFICA ---
    @Transactional(readOnly = true)
    public FrequenciaPercentualDTO calcularPercentual(UUID alunoId, UUID disciplinaId) {
        List<Frequencia> freqs = frequenciaRepository.findByAlunoAndDisciplina(alunoId, disciplinaId);
        if (freqs.isEmpty()) {
            throw new ResourceNotFoundException("Frequências do aluno na disciplina",
                    alunoId + "/" + disciplinaId);
        }

        Aluno aluno = freqs.get(0).getAluno();
        var disciplina = freqs.get(0).getAula().getTurmaDisciplina().getDisciplina();

        return montarPercentual(aluno, disciplina.getId(), disciplina.getNome(), freqs);
    }

    // --- PERCENTUAL POR DISCIPLINA, AGRUPANDO TODAS AS FREQUÊNCIAS DO ALUNO ---
    @Transactional(readOnly = true)
    public FrequenciaResumoDTO resumoDoAluno(UUID alunoId) {
        List<Frequencia> freqs = frequenciaRepository.findByAlunoId(alunoId);
        if (freqs.isEmpty()) {
            throw new ResourceNotFoundException("Frequências do aluno", alunoId);
        }

        Aluno aluno = freqs.get(0).getAluno();

        // --- AGRUPA POR DISCIPLINA ---
        Map<UUID, List<Frequencia>> porDisciplina = new LinkedHashMap<>();
        for (Frequencia f : freqs) {
            porDisciplina
                    .computeIfAbsent(f.getAula().getTurmaDisciplina().getDisciplina().getId(),
                            k -> new ArrayList<>())
                    .add(f);
        }

        List<FrequenciaPercentualDTO> linhas = porDisciplina.values().stream()
                .map(grupo -> {
                    var disciplina = grupo.get(0).getAula().getTurmaDisciplina().getDisciplina();
                    return montarPercentual(aluno, disciplina.getId(), disciplina.getNome(), grupo);
                })
                .toList();

        return FrequenciaResumoDTO.builder()
                .alunoId(aluno.getId())
                .alunoNome(aluno.getUsuario().getNome())
                .alunoMatriculaRA(aluno.getMatriculaRA())
                .disciplinas(linhas)
                .build();
    }

    // --- CALCULA O PERCENTUAL ---
    private FrequenciaPercentualDTO montarPercentual(Aluno aluno, UUID disciplinaId, String disciplinaNome,
                                                     List<Frequencia> freqs) {
        long total = freqs.size();
        long presentes = freqs.stream().filter(f -> Boolean.TRUE.equals(f.getPresente())).count();
        long faltas = total - presentes;

        BigDecimal percentual = (total == 0)
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(presentes)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        return FrequenciaPercentualDTO.builder()
                .alunoId(aluno.getId())
                .alunoNome(aluno.getUsuario().getNome())
                .disciplinaId(disciplinaId)
                .disciplinaNome(disciplinaNome)
                .totalAulas(total)
                .presencas(presentes)
                .faltas(faltas)
                .percentual(percentual)
                .build();
    }

    // --- CONVERSÃO AULA ---
    private AulaResponseDTO toAulaResponse(Aula a, int totalAlunos) {
        TurmaDisciplina td = a.getTurmaDisciplina();
        return AulaResponseDTO.builder()
                .id(a.getId())
                .turmaDisciplinaId(td.getId())
                .turmaId(td.getTurma().getId())
                .turmaCodigo(td.getTurma().getCodigo())
                .disciplinaId(td.getDisciplina().getId())
                .disciplinaNome(td.getDisciplina().getNome())
                .professorId(a.getProfessor().getId())
                .professorNome(a.getProfessor().getUsuario().getNome())
                .dataAula(a.getDataAula())
                .conteudoMinistrado(a.getConteudoMinistrado())
                .totalAlunos(totalAlunos)
                .criadaEm(a.getCriadaEm())
                .build();
    }

    // --- CONVERSÃO FREQUENCIA ---
    private FrequenciaResponseDTO toFrequenciaResponse(Frequencia f) {
        return FrequenciaResponseDTO.builder()
                .id(f.getId())
                .aulaId(f.getAula().getId())
                .dataAula(f.getAula().getDataAula())
                .alunoId(f.getAluno().getId())
                .alunoNome(f.getAluno().getUsuario().getNome())
                .alunoMatriculaRA(f.getAluno().getMatriculaRA())
                .presente(f.getPresente())
                .justificativa(f.getJustificativa())
                .build();
    }
}

