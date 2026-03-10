package rs.pravda.article_service.dto.homepage;

import lombok.Builder;
import rs.pravda.article_service.model.homepage.Section;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record UpdateHomePageDto(
        UUID categoryId,
        Map<Section, List<UUID>> sections
) {
}
