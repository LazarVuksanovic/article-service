package rs.pravda.article_service.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rs.pravda.article_service.dto.article.TranslatedArticle;
import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.service.CategoryService;
import rs.pravda.article_service.service.TagService;

import java.util.stream.Collectors;

import static rs.pravda.article_service.service.TranslationService.translateText;

@Component
@RequiredArgsConstructor
public class ArticleMapper {

    private final TagService tagService;
    private final CategoryService categoryService;

    public TranslatedArticle translate(Article article) {
        return TranslatedArticle.builder()
                .id(article.getId())
                .title(translateText(article.getTitle()))
                .content(translateText(article.getContent()))
                .teaser(translateText(article.getTeaser()))
                .slug(article.getSlug())
                .coverImageUrl(article.getCoverImageUrl())
                .author(article.getAuthor())
                .canonicalUrl(article.getCanonicalUrl())
                .category(categoryService.getTranslatedCategory(article.getCategory()))
                .createdAt(article.getCreatedAt())
                .publishedAt(article.getPublishedAt())
                .tags(article.getTags().stream()
                        .map(tagService::translate)
                        .collect(Collectors.toSet())
                )
                .build();
    }
}
