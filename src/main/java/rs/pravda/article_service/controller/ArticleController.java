package rs.pravda.article_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.pravda.article_service.dto.article.*;
import rs.pravda.article_service.exception.EntityNotFoundException;
import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.model.homepage.Section;
import rs.pravda.article_service.service.ArticleService;
import rs.pravda.article_service.service.PublishingService;

import java.util.UUID;

import static rs.pravda.article_service.dto.article.ArticleDto.toArticleDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final PublishingService publishArticle;

    @GetMapping
    Page<Article> getArticles(
            @ModelAttribute ArticledFilterDto filter,
            @PageableDefault Pageable pageable
    ) {
        return articleService.getArticles(filter, pageable);
    }

    @GetMapping("/translated")
    Page<TranslatedArticle> getArticlesTranslated(
            @ModelAttribute ArticledFilterDto filter,
            @PageableDefault Pageable pageable
    ) {
        return articleService.getArticlesTranslated(filter, pageable);
    }

    @GetMapping("/{id}")
    ArticleDto getArticle(@PathVariable UUID id) {
        return toArticleDto(articleService.getArticle(id).orElseThrow(() -> new EntityNotFoundException("Article")));
    }

    @GetMapping("/{id}/translated")
    TranslatedArticle getTranslatedArticle(@PathVariable UUID id) {
        return articleService.getTranslatedArticle(id);
    }

    @GetMapping("/slug/{slug}/translated")
    TranslatedArticle getArticleBySlug(@PathVariable String slug) {
        return articleService.getTranslatedArticleBySlug(slug);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ArticleDto createArticle(@RequestBody @Valid CreateArticleDto createArticle) {
        return toArticleDto(articleService.createArticle(createArticle));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateArticle(@PathVariable UUID id, @RequestBody @Valid UpdateArticleDto updateArticle) {
        articleService.updateArticle(id, updateArticle);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteArticle(@PathVariable UUID id) {
        articleService.deleteArticle(id);
    }

    @PutMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void archiveArticle(@PathVariable UUID id) {
        articleService.archiveArticle(id);
    }

    @PutMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void publishArticle(@PathVariable UUID id) {
        publishArticle.publishArticle(
                articleService.getArticle(id).orElseThrow(() -> new EntityNotFoundException("Article")),
                ArticlePublishConfig.builder()
                        .pushToCategory(true)
                        .pushToHome(true)
                        .homeSection(Section.MAIN)
                        .build());
    }

    @PutMapping("/{id}/hide")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void hiddeArticle(@PathVariable UUID id) {
        articleService.hideArticle(id);
    }
}
