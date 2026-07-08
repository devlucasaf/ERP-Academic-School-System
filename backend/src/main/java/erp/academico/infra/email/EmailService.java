package erp.academico.infra.email;

public interface EmailService {

    // --- ENVIA A SENHA TEMPORÁRIA GERADA PARA UM NOVO USUÁRIO ---
    void enviarSenhaTemporaria(String destinatario, String nome, String senhaTemporaria);
}

