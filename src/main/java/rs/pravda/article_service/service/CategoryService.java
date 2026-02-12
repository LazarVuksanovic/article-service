package rs.pravda.article_service.service;

import rs.pravda.article_service.dto.category.CategoriesFilterDto;
import rs.pravda.article_service.dto.category.CreateCategoryDto;
import rs.pravda.article_service.dto.category.TranslatedCategory;
import rs.pravda.article_service.dto.category.UpdateCategoryDto;
import rs.pravda.article_service.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryService extends TranslationService<Category, TranslatedCategory> {

    Optional<Category> getCategory(UUID id);

    TranslatedCategory getTranslatedCategory(UUID id);

    TranslatedCategory getTranslatedCategory(Category category);

    TranslatedCategory getTranslatedCategory(String slug);

    List<Category> getCategories(CategoriesFilterDto filter);

    Category createCategory(CreateCategoryDto category);

    void updateCategory(UUID id, UpdateCategoryDto category);

    void deleteCategory(UUID id);
}
