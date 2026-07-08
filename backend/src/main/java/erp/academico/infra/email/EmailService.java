package erp.academico.infra.email;

// --- SERVIÇO DE ENVIO DE E-MAILS DO SISTEMA ---
public interface EmailService {

    // --- ENVIA A SENHA TEMPORÁRIA GERADA PARA UM NOVO USUÁRIO ---
    void enviarSenhaTemporaria(String destinatario, String nome, String senhaTemporaria);
}

