package rs.pravda.article_service.dto.article;

import lombok.Builder;
import rs.pravda.article_service.model.homepage.Section;
import java.io.Serializable;

@Builder
public record ArticlePublishConfig(
        boolean pushToCategory,
        boolean pushToHome,
        Section homeSection
) implements Serializable {}