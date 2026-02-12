package rs.pravda.article_service.dto.category;

import jakarta.validation.constraints.NotEmpty;
import rs.pravda.article_service.localization.TranslatedText;

import java.util.UUID;

public record CreateCategoryDto(

        @NotEmpty
        TranslatedText name,

        UUID parentCategoryId
) {
}
