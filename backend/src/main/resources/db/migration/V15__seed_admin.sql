/* ============================================================
   V15 - SEED DO USUÁRIO ADMINISTRADOR INICIAL
   Cria o primeiro acesso ao sistema.

     email: admin@escola.com
     senha: admin123   (armazenada como hash BCrypt)

   TROQUE a senha no primeiro login em ambientes reais.
   ============================================================ */

-- --- INSERE O ADMIN APENAS SE AINDA NÃO EXISTIR ---
IF NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'admin@escola.com')
BEGIN
    INSERT INTO usuario (id, nome, email, senha, ativo, role, criadoEm, atualizadoEm)
    VALUES (
        NEWID(),
        'Administrador do Sistema',
        'admin@escola.com',
        -- --- HASH BCRYPT DE "admin123" ---
        '$2a$10$aa.40jD83U5l.i3xt.L49eC7VpeuHqNPO.b.qw3XmlomUAglpzUqC',
        1,
        'ADMIN',
        SYSUTCDATETIME(),
        SYSUTCDATETIME()
    );
END;

