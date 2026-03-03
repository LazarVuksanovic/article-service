package rs.pravda.article_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import rs.pravda.article_service.dto.article.*;
import rs.pravda.article_service.exception.EntityAlreadyExists;
import rs.pravda.article_service.exception.EntityNotFoundException;
import rs.pravda.article_service.localization.TranslatedText;
import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.model.homepage.Section;
import rs.pravda.article_service.repository.ArticleRepository;
import rs.pravda.article_service.repository.specification.ArticleSpecification;
import rs.pravda.article_service.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static rs.pravda.article_service.service.TranslationService.translateText;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final TagService tagService;
    private final CategoryService categoryService;
    private final ArticleRepository articleRepository;
    private final RevalidationService revalidationService;
    private final HomePageService homePageService;
    private final PublishingServiceImpl publishingService;
    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public Optional<Article> getArticle(UUID articleId) {
        Assert.notNull(articleId, "Article id must not be null");

        return articleRepository.findById(articleId);
    }

    @Override
    @Transactional(readOnly = true)
    public TranslatedArticle getTranslatedArticle(UUID id) {
        return getArticle(id)
                .map(this::translate)
                .orElseThrow(() -> new EntityNotFoundException("Article"));
    }

    @Override
    @Transactional(readOnly = true)
    public TranslatedArticle getTranslatedArticleBySlug(String slug) {
        log.info("Fetching article with slug: {}", slug);
        return articleRepository.findBySlug(slug)
                .map(this::translate)
                .orElseThrow(() -> new EntityNotFoundException("Article with slug: " + slug));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Article> getArticles(ArticledFilterDto filter, Pageable pageable) {
        return articleRepository.findAll(ArticleSpecification.createSpecification(filter), pageable);
    }

    @Override
    public List<Article> getArticles(List<UUID> articleIds) {
        return articleRepository.findByIdIn(articleIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TranslatedArticle> getArticlesTranslated(ArticledFilterDto filter, Pageable pageable) {
        return null;
    }

    @Override
    public Article createArticle(CreateArticleDto createArticle) {
        var slug = toSlug(createArticle.title());

        if (articleRepository.existsBySlug(slug))
            throw new EntityAlreadyExists("Article with slug");

        var category = categoryService.getCategory(createArticle.categoryId()).orElseThrow(() -> new EntityNotFoundException("Category"));
        var tags = new HashSet<>(tagService.getTags(createArticle.tagIds()));

        var article = Article.builder()
                .title(createArticle.title())
                .content(createArticle.content())
                .teaser(createArticle.teaser())
                .slug(slug)
                .coverImageUrl(createArticle.coverImageUrl())
                .author(createArticle.author())
                .canonicalUrl("/" + category.getSlug() + "/" + slug)
                .category(category)
                .tags(tags)
                .build();

        var publishConfig = ArticlePublishConfig.builder()
                .pushToCategory(createArticle.publishArticle().pushToCategory())
                .pushToHome(createArticle.publishArticle().pushToHome())
                .homeSection(createArticle.publishArticle().homeSection())
                .build();

        if (createArticle.publishArticle().publishDate() == null) {
            return publishingService.publishArticle(article, publishConfig);
        }

        var savedArticle = articleRepository.saveAndFlush(article);
        publishingService.scheduleArticle(savedArticle, createArticle.publishArticle().publishDate(), publishConfig);
        return savedArticle;
    }

    @Override
    public void updateArticle(UUID id, UpdateArticleDto updateArticle) {

        //TODO: ako ima sa istim slugom da se doda suffix??
        var article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article"));
        var category = categoryService.getCategory(updateArticle.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category"));
        var tags = new HashSet<>(tagService.getTags(updateArticle.tagIds()));

        article.setTitle(updateArticle.title());
        article.setContent(updateArticle.content());
        article.setTeaser(updateArticle.teaser());
        article.setCoverImageUrl(updateArticle.coverImageUrl());
        article.setAuthor(updateArticle.author());
        article.setCategory(category);
        article.setTags(tags);

        var savedArticle = articleRepository.save(article);

        revalidationService.revalidateArticle(savedArticle);
        homePageService.getAffectedCategories(article.getId()).forEach(revalidationService::revalidateHomePage);
    }

    @Override
    @Transactional
    public void deleteArticle(UUID articleId) {
        Assert.notNull(articleId, "Article id must not be null");

        var article = articleRepository.findById(articleId).orElseThrow(() -> new EntityNotFoundException("Article"));
        homePageService.removeArticleFromAllLayouts(articleId);
        revalidationService.revalidateArticle(article);
        articleRepository.delete(article);
    }

    @Override
    public void archiveArticle(UUID articleId) {
        var article = getArticle(articleId).orElseThrow(() -> new EntityNotFoundException("Article"));
        article.setArchived(true);
        articleRepository.save(article);
    }

    @Override
    @Transactional
    public void hideArticle(UUID articleId) {
        Assert.notNull(articleId, "Article id must not be null");

        var article = getArticle(articleId).orElseThrow(() -> new EntityNotFoundException("Article"));
        article.setPublishedAt(null);
        homePageService.removeArticleFromAllLayouts(articleId);
        revalidationService.revalidateArticle(article);
        articleRepository.save(article);
    }

    @Override
    public TranslatedArticle translate(Article article) {
        return TranslatedArticle.builder()
                .id(article.getId())
                .title(translateText(article.getTitle()))
                .content(translateText(article.getContent()))
                .teaser(translateText(article.getTeaser()))
                .slug(article.getSlug())
                .coverImageUrl(article.getCoverImageUrl())
                .author(article.getAuthor())
                .canonicalUrl(article.getCanonicalUrl())
                .category(categoryService.getTranslatedCategory(article.getCategory()))
                .createdAt(article.getCreatedAt())
                .publishedAt(article.getPublishedAt())
                .tags(article.getTags().stream()
                        .map(tagService::translate)
                        .collect(Collectors.toSet())
                )
                .build();
    }

    private String toSlug(TranslatedText title){
        var latTitle = title.get(Locale.forLanguageTag("sr-Latn"));
        return latTitle.toLowerCase()
                .replaceAll("[\\?\\!@#\\$%\\^&\\*\\(\\)_\\+=\"'`~\\[\\]\\{\\}\\|;:,\\.<>/\\\\]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "")
                .replaceAll("ć", "c")
                .replaceAll("š", "s")
                .replaceAll("đ", "dj")
                .replaceAll("č", "c")
                .replaceAll("ž", "z")
                + "-" + LocalDate.now();
    }
}
