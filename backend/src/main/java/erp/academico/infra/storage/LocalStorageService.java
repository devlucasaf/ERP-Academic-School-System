package erp.academico.infra.storage;

import erp.academico.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

// --- IMPLEMENTAÇÃO LOCAL DO STORAGE: SALVA OS ARQUIVOS NO FILESYSTEM (DEV) ---
// --- ATIVA SE app.storage.type = "local" (DEFAULT). EM PROD BASTA TROCAR PARA "s3". ---
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.storage", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final StorageProperties properties;

    private Path baseDir;

    // --- CRIA O DIRETÓRIO BASE NO STARTUP SE AINDA NÃO EXISTIR ---
    @PostConstruct
    public void init() {
        try {
            baseDir = Paths.get(properties.getLocal().getBasePath()).toAbsolutePath().normalize();
            Files.createDirectories(baseDir);
            log.info("LocalStorageService inicializado em: {}", baseDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível inicializar o diretório de storage local: " + ex.getMessage(), ex);
        }
    }

    // --- ARMAZENA O ARQUIVO EM {basePath}/{subDir}/{uuid-nomeOriginal} E RETORNA A URL PÚBLICA ---
    @Override
    public String store(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Arquivo enviado está vazio.");
        }

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "arquivo" : file.getOriginalFilename()
        );

        String safeName = UUID.randomUUID() + "-" + originalName.replaceAll("[^A-Za-z0-9._-]", "_");

        try {
            Path dir = baseDir.resolve(sanitizeSubDir(subDir)).normalize();
            if (!dir.startsWith(baseDir)) {
                throw new BusinessException("Caminho de armazenamento inválido.");
            }
            Files.createDirectories(dir);

            Path target = dir.resolve(safeName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // --- URL PÚBLICA RELATIVA (EX.: /files/atividades/uuid-arquivo.pdf) ---
            return properties.getLocal().getPublicBaseUrl() + "/" + sanitizeSubDir(subDir) + "/" + safeName;
        } catch (IOException ex) {
            log.error("Falha ao salvar arquivo no storage local", ex);
            throw new BusinessException("Falha ao salvar arquivo: " + ex.getMessage());
        }
    }

    // --- REMOVE O ARQUIVO A PARTIR DA URL PÚBLICA RETORNADA POR store(...) ---
    @Override
    public void delete(String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        String prefix = properties.getLocal().getPublicBaseUrl();
        if (!url.startsWith(prefix)) {
            log.warn("Tentativa de deletar URL fora do storage local: {}", url);
            return;
        }
        String relative = url.substring(prefix.length()).replaceFirst("^/+", "");
        try {
            Path target = baseDir.resolve(relative).normalize();
            if (!target.startsWith(baseDir)) {
                log.warn("Tentativa de deletar fora do baseDir: {}", target);
                return;
            }
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            log.warn("Falha ao deletar arquivo {}: {}", url, ex.getMessage());
        }
    }

    // --- IMPEDE QUE subDir CONTENHA "../" OU CAMINHOS ABSOLUTOS ---
    private String sanitizeSubDir(String subDir) {
        if (subDir == null || subDir.isBlank()) {
            return "outros";
        }
        return subDir.replaceAll("[^A-Za-z0-9/_-]", "_").replaceAll("^/+", "");
    }
}

