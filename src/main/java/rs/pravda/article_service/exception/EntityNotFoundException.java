package rs.pravda.article_service.exception;

import java.util.UUID;

import static java.lang.String.format;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity) {
        super(format("%s not found", entity));
    }
}
