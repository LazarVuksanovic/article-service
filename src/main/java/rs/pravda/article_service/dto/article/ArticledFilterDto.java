package rs.pravda.article_service.dto.article;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
public record ArticledFilterDto(
    String query,
    Boolean showHidden,
    Boolean showArchived,
    List<UUID> categoryIds,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime from,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime to
) {

    public ArticledFilterDto {
        if (showHidden == null) showHidden = false;
        if (showArchived == null) showArchived = false;
        if (categoryIds == null) categoryIds = new ArrayList<>();
    }
}
