package erp.academico.modules.funcionario.model;

import erp.academico.modules.usuario.model.RoleUsuario;

/**
 * Cargo administrativo do funcionário. Cada cargo mapeia diretamente para
 * uma {@link RoleUsuario} correspondente quando o usuário é criado.
 */
public enum CargoFuncionario {
    COORDENADOR,
    SECRETARIA,
    BIBLIOTECARIO,
    FINANCEIRO,
    ADMIN;

    public RoleUsuario toRoleUsuario() {
        return RoleUsuario.valueOf(this.name());
    }
}

