package erp.academico.infra.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// --- IMPLEMENTAÇÃO QUE APENAS REGISTRA O E-MAIL NO LOG (DESENVOLVIMENTO) ---
@Slf4j
@Service
public class LogEmailService implements EmailService {

    @Override
    public void enviarSenhaTemporaria(String destinatario, String nome, String senhaTemporaria) {
        // --- EM PRODUÇÃO, SUBSTITUIR POR ENVIO REAL (SMTP/PROVEDOR) ---
        log.info("""
                
                ========== E-MAIL (SIMULADO) ==========
                Para: {} <{}>
                Assunto: Bem-vindo ao ERP Acadêmico - Acesso ao sistema
                
                Olá, {}!
                Sua conta foi criada. Use a senha temporária abaixo para o primeiro acesso
                e altere-a assim que possível:
                
                    Senha temporária: {}
                
                =======================================
                """, nome, destinatario, nome, senhaTemporaria);
    }
}

