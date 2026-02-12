package rs.pravda.article_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.pravda.article_service.dto.category.*;
import rs.pravda.article_service.service.CategoryService;

import java.util.List;
import java.util.UUID;

import static rs.pravda.article_service.dto.category.CategoryDto.toCategoryDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    List<CategoryDto> getCategories(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID parentCategoryId
    ) {
        var filter = CategoriesFilterDto.builder()
                .query(query)
                .parentCategoryId(parentCategoryId)
                .build();
        return categoryService.getCategories(filter).stream().map(CategoryDto::toCategoryDto).toList();
    }

    @GetMapping("/{id}")
    TranslatedCategory getTranslatedCategory(@PathVariable UUID id) {
        return categoryService.getTranslatedCategory(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto createCategory(@RequestBody @Valid CreateCategoryDto createCategoryDto) {
        return toCategoryDto(categoryService.createCategory(createCategoryDto));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateCategory(@PathVariable UUID id, @RequestBody @Valid UpdateCategoryDto updateCategoryDto) {
        categoryService.updateCategory(id, updateCategoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }
}
