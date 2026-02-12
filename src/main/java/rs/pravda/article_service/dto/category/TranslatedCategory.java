package rs.pravda.article_service.dto.category;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TranslatedCategory(
        UUID id,
        String name,
        UUID parentCategoryId,
        String slug
) {
}
