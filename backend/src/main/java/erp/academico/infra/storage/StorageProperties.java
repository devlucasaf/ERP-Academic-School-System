package erp.academico.infra.storage;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    private String type = "local";
    private Local local = new Local();

    @Getter
    @Setter
    public static class Local {
        private String basePath = "./storage";

        private String publicBaseUrl = "/files";
    }
}

