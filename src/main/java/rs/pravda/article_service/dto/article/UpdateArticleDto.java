package rs.pravda.article_service.dto.article;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import rs.pravda.article_service.localization.TranslatedText;

import java.util.List;
import java.util.UUID;

public record UpdateArticleDto(

        @NotEmpty
        TranslatedText title,

        @NotEmpty
        TranslatedText content,

        @NotEmpty
        TranslatedText teaser,

        @NotNull
        UUID categoryId,

        String author,
        List<UUID> tagIds,
        String coverImageUrl
) {
}
