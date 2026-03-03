package rs.pravda.article_service.service;

import rs.pravda.article_service.dto.homepage.HomePageDto;
import rs.pravda.article_service.dto.homepage.UpdateHomePageDto;
import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.model.Category;
import rs.pravda.article_service.model.homepage.Section;

import java.util.List;
import java.util.UUID;

public interface HomePageService {

    HomePageDto getHomePageData(String categorySlug);

    void updateLayout(UpdateHomePageDto dto);

    void removeArticleFromAllLayouts(UUID articleId);

    List<Category> getAffectedCategories(UUID articleId);

    void addArticleToLayout(Article article, Category category, Section section, int position);
}