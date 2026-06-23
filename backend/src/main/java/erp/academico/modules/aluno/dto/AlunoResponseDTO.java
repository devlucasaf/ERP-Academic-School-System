package erp.academico.modules.aluno.dto;

import erp.academico.modules.aluno.model.StatusAluno;
import erp.academico.modules.usuario.dto.UsuarioResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoResponseDTO {

    private UUID                id;
    private UsuarioResponseDTO  usuario;
    private String              matriculaRA;
    private LocalDate           dataIngresso;
    private StatusAluno         status;
    private UUID                turmaAtualId;
    private String              observacoes;
    private LocalDateTime       criadoEm;
    private LocalDateTime       atualizadoEm;
}

