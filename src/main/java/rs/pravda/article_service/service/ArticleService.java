package rs.pravda.article_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.pravda.article_service.dto.article.ArticledFilterDto;
import rs.pravda.article_service.dto.article.CreateArticleDto;
import rs.pravda.article_service.dto.article.TranslatedArticle;
import rs.pravda.article_service.dto.article.UpdateArticleDto;
import rs.pravda.article_service.model.Article;

import java.util.Optional;
import java.util.UUID;

public interface ArticleService extends TranslationService<Article, TranslatedArticle> {

    Optional<Article> getArticle(UUID articleId);

    TranslatedArticle getTranslatedArticle(UUID articleId);

    TranslatedArticle getTranslatedArticleBySlug(String slug);

    Page<Article> getArticles(ArticledFilterDto filter, Pageable pageable);

    Page<TranslatedArticle> getArticlesTranslated(ArticledFilterDto filter, Pageable pageable);

    Article createArticle(CreateArticleDto createArticle);

    void updateArticle(UUID id, UpdateArticleDto createArticle);

    void deleteArticle(UUID articleId);

    void archiveArticle(UUID articleId);

    void publishArticle(UUID articleId);

    void hideArticle(UUID articleId);
}
