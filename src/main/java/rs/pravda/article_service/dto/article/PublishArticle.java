package rs.pravda.article_service.dto.article;

import rs.pravda.article_service.model.homepage.Section;

import java.time.LocalDateTime;

public record PublishArticle(
        LocalDateTime publishDate,
        boolean pushToCategory,
        boolean pushToHome,
        Section homeSection
) {
}