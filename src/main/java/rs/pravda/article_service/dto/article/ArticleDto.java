package rs.pravda.article_service.dto.article;

import lombok.Builder;
import rs.pravda.article_service.dto.category.CategoryDto;
import rs.pravda.article_service.localization.TranslatedText;
import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.model.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static rs.pravda.article_service.dto.category.CategoryDto.toCategoryDto;

@Builder
public record ArticleDto(
        UUID id,
        boolean archived,
        TranslatedText title,
        TranslatedText content,
        TranslatedText teaser,
        String slug,
        String coverImageUrl,
        String author,
        String canonicalUrl,
        boolean premium,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        CategoryDto category,
        List<Tag> tags,
        List<Locale> supportedLanguages
) {

    public static ArticleDto toArticleDto(Article article){
        if (article == null) {
            return null;
        }
        return ArticleDto.builder()
                .id(article.getId())
                .archived(article.isArchived())
                .title(article.getTitle())
                .content(article.getContent())
                .teaser(article.getTeaser())
                .slug(article.getSlug())
                .coverImageUrl(article.getCoverImageUrl())
                .author(article.getAuthor())
                .canonicalUrl(article.getCanonicalUrl())
                .premium(article.isPremium())
                .createdAt(article.getCreatedAt())
                .category(toCategoryDto(article.getCategory()))
                .publishedAt(article.getPublishedAt())
                .tags(article.getTags().stream().toList())
                .supportedLanguages(article.getContent().keySet().stream().toList())
                .build();
    }
}
