package rs.pravda.article_service.dto.tag;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import rs.pravda.article_service.localization.TranslatedText;

@Builder
public record UpdateTagDto(
        @NotNull
        TranslatedText name
) {
}
