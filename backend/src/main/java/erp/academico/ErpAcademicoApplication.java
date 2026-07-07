package erp.academico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ErpAcademicoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpAcademicoApplication.class, args);
    }

}

