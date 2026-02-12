package rs.pravda.article_service.dto.homePage;

import lombok.Builder;
import rs.pravda.article_service.dto.article.TranslatedArticle;
import rs.pravda.article_service.dto.category.TranslatedCategory;

import java.util.List;

@Builder
public record HomePageDto(
    List<TranslatedArticle> main,
    List<TranslatedArticle> side,
    List<TranslatedCategory> navBarCategories
) {
}
