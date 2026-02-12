package rs.pravda.article_service.dto.homePage;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record HomePageUpdateDto(
        UUID categoryId,
        List<UUID> navCategoryIds,
        List<UUID> mainArticleIds,
        List<UUID> sideArticleIds
) {}