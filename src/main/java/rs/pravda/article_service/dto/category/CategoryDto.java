package rs.pravda.article_service.dto.category;

import lombok.Builder;
import rs.pravda.article_service.localization.TranslatedText;
import rs.pravda.article_service.model.Category;

import java.util.UUID;

@Builder
public record CategoryDto(
        UUID id,
        UUID parentCategoryId,
        TranslatedText name,
        String slug
){

    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryDto.builder()
                .id(category.getId())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .name(category.getName())
                .build();
    }
}
