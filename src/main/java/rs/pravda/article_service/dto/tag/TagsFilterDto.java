package rs.pravda.article_service.dto.tag;

import lombok.Builder;

@Builder
public record TagsFilterDto(
        String query
) {
}
