package rs.pravda.article_service.exception;

public class EntityAlreadyExists extends RuntimeException {
    public EntityAlreadyExists(String entity) {
        super(entity + " already exists.");
    }
}
