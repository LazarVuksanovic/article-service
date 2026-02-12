package rs.pravda.article_service.service;

import rs.pravda.article_service.dto.homePage.HomePageDto;
import rs.pravda.article_service.dto.homePage.HomePageUpdateDto;
import rs.pravda.article_service.model.homepage.HomePage;

import java.util.List;
import java.util.UUID;

public interface HomePageService {

    HomePageDto getHomePageData(String categorySlug);

    void updateLayout(HomePageUpdateDto dto);

    void removeArticleFromAllLayouts(UUID articleId);

    List<HomePage> getAffectedHomePages(UUID articleId);
}
