package erp.academico.infra.seed;

import erp.academico.modules.usuario.model.RoleUsuario;
import erp.academico.modules.usuario.model.Usuario;
import erp.academico.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// --- CRIA O USUARIO ADMIN INICIAL SE AINDA NAO EXISTIR (SUBSTITUI O SEED SQL) ---
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@escola.com";
    private static final String ADMIN_SENHA = "admin123";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // --- IDEMPOTENTE: SO CRIA UMA VEZ ---
        if (usuarioRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        Usuario admin = Usuario.builder()
                .nome("Administrador")
                .email(ADMIN_EMAIL)
                .senha(passwordEncoder.encode(ADMIN_SENHA))
                .ativo(true)
                .role(RoleUsuario.ADMIN)
                .build();

        usuarioRepository.save(admin);
        log.info("Usuario admin inicial criado: {} / {}", ADMIN_EMAIL, ADMIN_SENHA);
    }
}

