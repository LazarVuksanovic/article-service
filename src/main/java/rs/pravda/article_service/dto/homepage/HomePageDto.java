package rs.pravda.article_service.dto.homepage;

import lombok.Builder;
import rs.pravda.article_service.dto.article.TranslatedArticle;

import java.util.List;
import java.util.Map;

@Builder
public record HomePageDto(
        Map<String, List<TranslatedArticle>> sections
) {
}