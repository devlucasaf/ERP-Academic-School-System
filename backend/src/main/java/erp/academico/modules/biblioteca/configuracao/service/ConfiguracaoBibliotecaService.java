package erp.academico.modules.biblioteca.configuracao.service;

import erp.academico.exception.BusinessException;
import erp.academico.modules.biblioteca.configuracao.dto.ConfiguracaoBibliotecaDTO;
import erp.academico.modules.biblioteca.configuracao.model.ConfiguracaoBiblioteca;
import erp.academico.modules.biblioteca.configuracao.repository.ConfiguracaoBibliotecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfiguracaoBibliotecaService {

    private final ConfiguracaoBibliotecaRepository repository;

    // --- RETORNA A ÚNICA LINHA DE CONFIGURAÇÃO ---
    @Transactional(readOnly = true)
    public ConfiguracaoBiblioteca obter() {
        return repository.findAll().stream().findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Configuração da biblioteca não inicializada. Verifique a migration V14."));
    }

    // --- ATUALIZA OS PARÂMETROS GLOBAIS ---
    @Transactional
    public ConfiguracaoBiblioteca atualizar(ConfiguracaoBibliotecaDTO dto) {
        ConfiguracaoBiblioteca cfg = obter();
        cfg.setPrazoEmprestimoAluno(dto.getPrazoEmprestimoAluno());
        cfg.setPrazoEmprestimoProfessor(dto.getPrazoEmprestimoProfessor());
        cfg.setMaxEmprestimosSimultaneos(dto.getMaxEmprestimosSimultaneos());
        cfg.setMaxRenovacoes(dto.getMaxRenovacoes());
        cfg.setValorMultaDia(dto.getValorMultaDia());
        return repository.save(cfg);
    }
}

