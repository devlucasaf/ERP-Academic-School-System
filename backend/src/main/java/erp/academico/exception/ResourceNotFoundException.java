package erp.academico.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Object id) {
        super("%s não encontrado(a) com identificador: %s".formatted(resource, id));
    }
}

