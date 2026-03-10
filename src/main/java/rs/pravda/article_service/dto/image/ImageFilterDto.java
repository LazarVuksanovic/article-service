package rs.pravda.article_service.dto.image;

import java.util.List;
import java.util.UUID;

public record ImageFilterDto(
        String filenameQuery,
        List<UUID> tagIds
) {
}
