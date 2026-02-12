package rs.pravda.article_service.dto.tag;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TranslatedTag(
        UUID id,
        String name
) {
}
