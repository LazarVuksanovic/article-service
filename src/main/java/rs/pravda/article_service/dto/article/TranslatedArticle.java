package rs.pravda.article_service.dto.article;

import lombok.Builder;
import rs.pravda.article_service.dto.category.TranslatedCategory;
import rs.pravda.article_service.dto.tag.TranslatedTag;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record TranslatedArticle(
        UUID id,
        String title,
        String content,
        String teaser,
        String slug,
        String coverImageUrl,
        String author,
        String canonicalUrl,
        TranslatedCategory category,
        Set<TranslatedTag> tags,
        LocalDateTime createdAt,
        LocalDateTime publishedAt
) {
}
