package erp.academico.infra.storage;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// --- PROPRIEDADES CONFIGURÁVEIS DO STORAGE (LIDAS DO application.yml -> app.storage.*) ---
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    // --- TIPO DE STORAGE ATIVO (HOJE SUPORTAMOS APENAS "local") ---
    private String type = "local";

    private Local local = new Local();

    @Getter
    @Setter
    public static class Local {
        // --- DIRETÓRIO RAIZ ONDE OS ARQUIVOS SERÃO PERSISTIDOS ---
        private String basePath = "./storage";

        // --- PREFIXO DA URL PÚBLICA QUE O FRONT USARÁ PARA BAIXAR OS ARQUIVOS ---
        private String publicBaseUrl = "/files";
    }
}

