package rs.pravda.article_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import rs.pravda.article_service.dto.category.CategoriesFilterDto;
import rs.pravda.article_service.dto.category.CreateCategoryDto;
import rs.pravda.article_service.dto.category.TranslatedCategory;
import rs.pravda.article_service.dto.category.UpdateCategoryDto;
import rs.pravda.article_service.exception.EntityAlreadyExists;
import rs.pravda.article_service.exception.EntityNotFoundException;
import rs.pravda.article_service.model.Category;
import rs.pravda.article_service.repository.CategoryRepository;
import rs.pravda.article_service.service.CategoryService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static rs.pravda.article_service.service.TranslationService.translateText;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Optional<Category> getCategory(UUID id) {
        Assert.notNull(id, "Category id must not be null");

        return categoryRepository.findById(id);
    }

    @Override
    public TranslatedCategory getTranslatedCategory(UUID id) {
        return getCategory(id)
                .map(this::translate)
                .orElseThrow(() -> new EntityNotFoundException("Category"));
    }

    @Override
    public TranslatedCategory getTranslatedCategory(Category category) {
        Assert.notNull(category, "Category must not be null");

        return translate(category);
    }

    @Override
    public TranslatedCategory getTranslatedCategory(String slug) {
        Assert.hasText(slug, "Category slug must not be empty");

        return categoryRepository.findBySlug(slug)
                .map(this::translate)
                .orElseThrow(() -> new EntityNotFoundException("Category"));
    }

    @Override
    public List<Category> getCategories(CategoriesFilterDto filter) {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(CreateCategoryDto createCategory) {
        //todo: testirati ovo
        createCategory.name().forEach((lang, name) -> {
            if(name == null || name.trim().isEmpty())
                throw new IllegalArgumentException("Category name translation for language [" + lang + "] cannot be null or empty");

            if(categoryRepository.existsNameTranslation(lang.toLanguageTag(), name))
                throw new EntityAlreadyExists("Category translation [" + lang + "]");
        });

        var newCategory = Category.builder()
                .name(createCategory.name())
                .parentCategory(null)
                .slug(toSlug(createCategory.name().get(Locale.forLanguageTag("sr-Latn"))))
                .build();

        if(createCategory.parentCategoryId() != null){
            var parentCategory = this.getCategory(createCategory.parentCategoryId()).orElseThrow(() -> new EntityNotFoundException("Parent category"));
            newCategory.setParentCategory(parentCategory);
        }

        return categoryRepository.save(newCategory);
    }

    @Override
    public void updateCategory(UUID id, UpdateCategoryDto updateCategory) {
        var category = getCategory(id).orElseThrow(() -> new EntityNotFoundException("Category"));
        Category parentCategory = null;

        if(updateCategory.parentCategoryId() != null){
            if(category.getId().equals(updateCategory.parentCategoryId()))
                throw new IllegalArgumentException("Category cannot be its own parent");

            parentCategory = getCategory(updateCategory.parentCategoryId()).orElseThrow(() -> new EntityNotFoundException("Parent category"));
        }

        updateCategory.name().forEach((lang, name) -> {
            if(name == null || name.trim().isEmpty())
                throw new IllegalArgumentException("Category name translation for language [" + lang + "] cannot be null or empty");

            if(categoryRepository.existsByNameInLocaleExcludingId(lang.toLanguageTag(), name, id))
                throw new EntityAlreadyExists("Category translation [" + lang + "]");
        });

        category.setName(updateCategory.name());
        category.setParentCategory(parentCategory);

        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        Assert.notNull(id, "Category id must not be null");

        var subcategories = categoryRepository.findByParentCategoryId(id);
        if(!subcategories.isEmpty())
            throw new IllegalArgumentException("Category has subcategories and cannot be deleted");

        categoryRepository.deleteById(id);
    }

    @Override
    public TranslatedCategory translate(Category category) {
        return TranslatedCategory.builder()
                .id(category.getId())
                .name(translateText(category.getName()))
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .slug(category.getSlug())
                .build();
    }

    public static String toSlug(String name) {
        if(name == null) return null;
        return name.trim().toLowerCase().replaceAll("\\s+", "-");
    }
}
