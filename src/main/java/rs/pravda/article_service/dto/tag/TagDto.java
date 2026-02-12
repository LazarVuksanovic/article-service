package rs.pravda.article_service.dto.tag;

import lombok.Builder;
import rs.pravda.article_service.localization.TranslatedText;
import rs.pravda.article_service.model.Tag;

import java.util.UUID;

@Builder
public record TagDto(
        UUID id,
        TranslatedText name
) {

    public static TagDto toTagDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
