package rs.pravda.article_service.dto.auth;

public record AuthRequest(
        String email,
        String password
) {
}