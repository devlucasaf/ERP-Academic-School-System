package erp.academico.infra.storage;

import org.springframework.web.multipart.MultipartFile;

// --- ABSTRAÇÃO DE ARMAZENAMENTO DE ARQUIVOS ---
// --- IMPLEMENTAÇÕES POSSÍVEIS: LOCAL (DEV) E S3 (PROD) ---
public interface StorageService {

    // --- ARMAZENA O ARQUIVO E RETORNA A URL PÚBLICA DE ACESSO ---
    String store(MultipartFile file, String subDir);

    // --- REMOVE O ARQUIVO A PARTIR DA URL RETORNADA POR store(...) ---
    void delete(String url);
}

