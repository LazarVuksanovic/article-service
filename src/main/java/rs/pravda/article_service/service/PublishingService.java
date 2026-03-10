package rs.pravda.article_service.service;

import rs.pravda.article_service.dto.article.ArticlePublishConfig;
import rs.pravda.article_service.model.Article;

import java.time.LocalDateTime;

public interface PublishingService {

    void scheduleArticle(Article article, LocalDateTime scheduledAt, ArticlePublishConfig config);

    void processDueArticles();

    Article publishArticle(Article article, ArticlePublishConfig config);
}
