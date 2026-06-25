package erp.academico.infra.storage;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

// --- EXPÕE O DIRETÓRIO DE STORAGE LOCAL COMO RECURSO HTTP ESTÁTICO ---
// --- EX.: /api/files/atividades/uuid.pdf -> {basePath}/atividades/uuid.pdf ---
@Configuration
@RequiredArgsConstructor
public class StorageWebConfig implements WebMvcConfigurer {

    private final StorageProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String publicBaseUrl = properties.getLocal().getPublicBaseUrl();
        String location = "file:" + Paths.get(properties.getLocal().getBasePath())
                .toAbsolutePath().normalize() + "/";

        registry.addResourceHandler(publicBaseUrl + "/**")
                .addResourceLocations(location);
    }
}

