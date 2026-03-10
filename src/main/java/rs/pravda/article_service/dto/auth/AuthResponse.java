package rs.pravda.article_service.dto.auth;

import lombok.Builder;
import rs.pravda.article_service.model.auth.Role;

@Builder
public record AuthResponse(
        String accessToken,
        String firstName,
        String lastName,
        String email,
        Role role
) {
}