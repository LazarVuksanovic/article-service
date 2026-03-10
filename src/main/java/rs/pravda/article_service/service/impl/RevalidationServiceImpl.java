package rs.pravda.article_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.pravda.article_service.client.FrontendClient;
import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.model.Category;
import rs.pravda.article_service.service.RevalidationService;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RevalidationServiceImpl implements RevalidationService {

    private final FrontendClient frontendClient;

    @Value("${application.frontend.revalidate-secret}")
    private String revalidateSecret;

    @Override
    public void revalidateArticle(Article article) {
        var slugValue = article.getSlug();
        if (slugValue != null && !slugValue.isBlank()) {
            revalidate(
                    "article-" + slugValue,
                    "/" + article.getCategory().getSlug() + "/" + slugValue
            );
            revalidate(
                    "article-" + slugValue,
                    "/lat/" + article.getCategory().getSlug() + "/" + slugValue
            );
        }
    }

    @Override
    public void revalidateHomePage(Category category) {
        var categorySlug = category != null ? category.getSlug() : "";
        var tag = categorySlug.isEmpty() ?  "homepage" : "homepage-" + categorySlug;

        revalidate(tag, "/" + categorySlug);
        revalidate(tag, "/lat" + (categorySlug.isEmpty() ? "" : "/" + categorySlug));
    }

    private void revalidate(String tag, String path) {
        try {
            frontendClient.triggerRevalidation(Map.of(
                    "tag", tag != null ? tag : "",
                    "path", path != null ? path : "",
                    "secret", revalidateSecret
            ));
            log.info("Successful revalidation for tag: {} path: {}", tag, path);
        } catch (Exception e) {
            log.error("Failed revalidation for tag: {} path: {}. Message: {}", tag, path, e.getMessage());
        }
    }
}