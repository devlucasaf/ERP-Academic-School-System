/* ============================================================
   ERP-Academic-School-System - SETUP INICIAL DO BANCO
   Execute este script UMA ÚNICA VEZ no SSMS conectado como
   um login administrador (ex.: 'sa') do SQL Server 2019+.

   O que ele faz:
     1. Cria o banco  "erp_academic_db"  (se ainda não existir)
     2. Cria o login  "erp_academic_user" (autenticação SQL)
     3. Cria o usuário do banco e concede db_owner
     4. Garante que a autenticação SQL (mixed mode) esteja ativa
   ============================================================ */

-- --- 1. CRIA O BANCO SE NÃO EXISTIR ---
IF DB_ID('erp_academic_db') IS NULL
BEGIN
    CREATE DATABASE erp_academic_db;
    PRINT 'Banco erp_academic_db criado.';
END
ELSE
    PRINT 'Banco erp_academic_db ja existe. Nenhuma acao.';
GO

--     TROQUE a senha abaixo em ambientes reais!
IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'erp_academic_user')
BEGIN
    CREATE LOGIN erp_academic_user
        WITH PASSWORD          = 'Erp@Academic#2025!',
             CHECK_POLICY       = ON,
             DEFAULT_DATABASE   = erp_academic_db;
    PRINT 'Login erp_academic_user criado.';
END
ELSE
    PRINT 'Login erp_academic_user ja existe. Nenhuma acao.';
GO

-- --- 3. CRIA O USUÁRIO DO BANCO E CONCEDE db_owner ---
USE erp_academic_db;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'erp_academic_user')
BEGIN
    CREATE USER erp_academic_user FOR LOGIN erp_academic_user;
    PRINT 'Usuario erp_academic_user criado no banco.';
END
ELSE
    PRINT 'Usuario erp_academic_user ja existe no banco. Nenhuma acao.';
GO

-- --- CONCEDE PERMISSÃO TOTAL (db_owner) SOBRE O BANCO ---
ALTER ROLE db_owner ADD MEMBER erp_academic_user;
PRINT 'Permissao db_owner concedida a erp_academic_user.';
GO

/* ------------------------------------------------------------
   OBSERVAÇÃO SOBRE AUTENTICAÇÃO SQL (MIXED MODE)
   O login SQL só funciona se a instância estiver em
   "SQL Server and Windows Authentication mode".
   Para verificar / habilitar via SSMS:
     - Clique com o botão direito na instância > Properties
       > Security > "SQL Server and Windows Authentication mode"
     - Reinicie o serviço do SQL Server.
   (A troca de modo NÃO pode ser feita 100% por T-SQL; use a UI.)
   ------------------------------------------------------------ */

