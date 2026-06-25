package erp.academico.modules.funcionario.model;

import erp.academico.modules.usuario.model.RoleUsuario;

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

