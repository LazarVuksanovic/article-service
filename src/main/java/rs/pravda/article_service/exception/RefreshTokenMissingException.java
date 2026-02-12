package rs.pravda.article_service.exception;

public class RefreshTokenMissingException extends RuntimeException {
    public RefreshTokenMissingException() {
        super("Refresh token is missing.");
    }
}
