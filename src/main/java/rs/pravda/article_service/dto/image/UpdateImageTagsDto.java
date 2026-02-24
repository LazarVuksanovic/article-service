package rs.pravda.article_service.dto.image;

import java.util.List;
import java.util.UUID;

public record UpdateImageTagsDto(
        List<UUID> tagIds
) {}