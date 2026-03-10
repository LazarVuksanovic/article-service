package rs.pravda.article_service.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequest(
        // The raw Google ID token returned by Google One Tap / GIS popup
        @NotBlank
        String credential
) {}