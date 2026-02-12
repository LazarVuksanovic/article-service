package rs.pravda.article_service.dto.auth;

import rs.pravda.article_service.model.auth.Role;

public record AuthResponse(
        String accessToken,
        String firstName,
        String lastName,
        String email,
        Role role
) {
}