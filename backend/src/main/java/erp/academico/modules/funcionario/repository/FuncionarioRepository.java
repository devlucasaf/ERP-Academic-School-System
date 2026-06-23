package erp.academico.modules.funcionario.repository;

import erp.academico.modules.funcionario.model.CargoFuncionario;
import erp.academico.modules.funcionario.model.Funcionario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {

    Page<Funcionario> findByCargo(CargoFuncionario cargo, Pageable pageable);
}

