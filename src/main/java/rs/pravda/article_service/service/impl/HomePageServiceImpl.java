package rs.pravda.article_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import rs.pravda.article_service.dto.homepage.HomePageDto;
import rs.pravda.article_service.dto.homepage.UpdateHomePageDto;
import rs.pravda.article_service.exception.EntityNotFoundException;
import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.model.Category;
import rs.pravda.article_service.model.homepage.ArticleSection;
import rs.pravda.article_service.model.homepage.Section;
import rs.pravda.article_service.repository.ArticleRepository;
import rs.pravda.article_service.repository.ArticleSectionRepository;
import rs.pravda.article_service.repository.CategoryRepository;
import rs.pravda.article_service.service.HomePageService;
import rs.pravda.article_service.service.RevalidationService;
import rs.pravda.article_service.service.mapper.ArticleMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HomePageServiceImpl implements HomePageService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final ArticleSectionRepository articleSectionRepository;
    private final ArticleMapper articleMapper;
    private final RevalidationService revalidationService;
    private static final Logger log = LoggerFactory.getLogger(HomePageServiceImpl.class);

    @Override
    public HomePageDto getHomePageData(String categorySlug) {
        log.info("Fetching homepage layout for: {}", categorySlug);

        var category = resolveCategory(categorySlug);
        var sections = articleSectionRepository.findByCategory(category);
        var groupedArticles = sections.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getSection().name(),
                        Collectors.mapping(
                                s -> articleMapper.translate(s.getArticle()),
                                Collectors.toList()
                        )
                ));

        return HomePageDto.builder()
                .sections(groupedArticles)
                .build();
    }

    @Override
    @Transactional
    public void updateLayout(UpdateHomePageDto dto) {
        Assert.notNull(dto.sections(), "Sections cannot be null");

        Category category = null;
        if (dto.categoryId() != null) {
            category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category"));
        }

        articleSectionRepository.deleteByCategory(category);
        var newSections = createNewSections(category, dto.sections());
        articleSectionRepository.saveAll(newSections);

        revalidationService.revalidateHomePage(category);
    }

    @Override
    @Transactional
    public void removeArticleFromAllLayouts(UUID articleId) {
        var affected = getAffectedCategories(articleId);

        List<ArticleSection> entriesToDelete = articleSectionRepository.findByArticleId(articleId);
        articleSectionRepository.deleteAll(entriesToDelete);

        affected.forEach(revalidationService::revalidateHomePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAffectedCategories(UUID articleId) {
        return articleSectionRepository.findByArticleId(articleId).stream()
                .map(ArticleSection::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addArticleToLayout(Article article, Category category, Section section, int position) {
        List<ArticleSection> existing = articleSectionRepository.findByCategory(category).stream()
                .filter(articleInSection -> section.equals(articleInSection.getSection()))
                .filter(articleInSection -> articleInSection.getOrder() >= position)
                .map(ArticleSection::increaseOrder)
                .toList();

        var newEntry = ArticleSection.builder()
                .article(article)
                .category(category)
                .section(section)
                .order(position)
                .build();

        articleSectionRepository.saveAll(existing);
        articleSectionRepository.save(newEntry);
    }

    private List<ArticleSection> createNewSections(Category category, Map<Section, List<UUID>> sectionsMap) {
        return sectionsMap.entrySet().stream()
                .flatMap(entry -> mapToArticleSections(category, entry.getKey(), entry.getValue()))
                .toList();
    }

    private Stream<ArticleSection> mapToArticleSections(Category category, Section section, List<UUID> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) return Stream.empty();

        var articlesMap = articleRepository.findAllById(articleIds).stream()
                .collect(Collectors.toMap(Article::getId, article -> article));

        return IntStream.range(0, articleIds.size())
                .mapToObj(i -> {
                    var id = articleIds.get(i);
                    var article = articlesMap.get(id);

                    return ArticleSection.builder()
                            .category(category)
                            .article(article)
                            .section(section)
                            .order(i+1)
                            .build();
                });
    }

    private Category resolveCategory(String categorySlug) {
        if (categorySlug == null || categorySlug.isBlank() || categorySlug.equalsIgnoreCase("global")) {
            return null;
        }
        return categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new EntityNotFoundException("Category with slug: " + categorySlug));
    }
}