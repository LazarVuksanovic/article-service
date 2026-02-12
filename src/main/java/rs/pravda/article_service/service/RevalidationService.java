package rs.pravda.article_service.service;

import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.model.homepage.HomePage;

public interface RevalidationService {

    void revalidateArticle(Article article);

    void revalidateHomePage(HomePage homePage);
}
