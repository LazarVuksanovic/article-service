package rs.pravda.article_service.dto.category;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CategoriesFilterDto(
        String query,
        UUID parentCategoryId
) {
}
