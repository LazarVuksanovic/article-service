package rs.pravda.article_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.pravda.article_service.client.FrontendClient;
import rs.pravda.article_service.dto.homePage.HomePageDto;
import rs.pravda.article_service.dto.homePage.HomePageUpdateDto;
import rs.pravda.article_service.exception.EntityNotFoundException;
import rs.pravda.article_service.service.mapper.ArticleMapper;
import rs.pravda.article_service.model.homepage.HomePage;
import rs.pravda.article_service.repository.ArticleRepository;
import rs.pravda.article_service.repository.CategoryRepository;
import rs.pravda.article_service.repository.HomePageRepository;
import rs.pravda.article_service.service.CategoryService;
import rs.pravda.article_service.service.HomePageService;
import rs.pravda.article_service.service.RevalidationService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomePageServiceImpl implements HomePageService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final HomePageRepository homePageRepository;
    private final ArticleMapper articleMapper;
    private final CategoryService categoryService;
    private final RevalidationService revalidationService;
    private static final Logger log = LoggerFactory.getLogger(HomePageServiceImpl.class);

    private final FrontendClient frontendClient;

    @Value("${application.frontend.revalidate-secret}")
    private String revalidateSecret;

    @Override
    public HomePageDto getHomePageData(String categorySlug) {

        log.info("Fetching homepage data: {}", categorySlug);
        var homePage = getHomePageForCategory(categorySlug).orElseThrow(() -> new EntityNotFoundException("HomePage config not found"));

        return HomePageDto.builder()
                .navBarCategories(homePage.getNavCategories() != null
                        ? homePage.getNavCategories().stream().map(categoryService::translate).toList()
                        : null
                )
                .main(homePage.getMain().stream().map(articleMapper::translate).toList())
                .side(homePage.getSide().stream().map(articleMapper::translate).toList())
                .build();
    }

    @Override
    @Transactional
    public void updateLayout(HomePageUpdateDto dto) {
        var homePage = getHomePageForCategory(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("HomePage config not found"));

        // Update Nav Categories (Only if global)
        if (dto.categoryId() == null && dto.navCategoryIds() != null && !dto.navCategoryIds().isEmpty()) {
            homePage.setNavCategories(categoryRepository.findAllById(dto.navCategoryIds()));
        }

        // Update Articles (Maintain order from DTO)
        homePage.setMain(dto.mainArticleIds().stream()
                .map(articleRepository::getReferenceById)
                .collect(Collectors.toList()));

        homePage.setSide(dto.sideArticleIds().stream()
                .map(articleRepository::getReferenceById)
                .collect(Collectors.toList()));

        homePageRepository.save(homePage);

        revalidationService.revalidateHomePage(homePage);
    }


    @Override
    @Transactional
    public void removeArticleFromAllLayouts(UUID articleId) {
        homePageRepository.findAll().forEach(hp -> {
            boolean removedMain = hp.getMain().removeIf(a -> a.getId().equals(articleId));
            boolean removedSide = hp.getSide().removeIf(a -> a.getId().equals(articleId));

            if (removedMain || removedSide) {
                homePageRepository.save(hp);
                // Trigger revalidation for the specific homepage changed
                revalidationService.revalidateHomePage(hp);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomePage> getAffectedHomePages(UUID articleId) {
        // Find all homepages where this article is present in either section
        return homePageRepository.findAll().stream()
                .filter(hp -> hp.getMain().stream().anyMatch(a -> a.getId().equals(articleId))
                        || hp.getSide().stream().anyMatch(a -> a.getId().equals(articleId)))
                .collect(Collectors.toList());
    }

    private Optional<HomePage> getHomePageForCategory(UUID categoryId){
        return switch (categoryId) {
            case null -> homePageRepository.findByCategoryIsNull();
            default -> homePageRepository.findByCategoryId(categoryId);
        };
    }

    private Optional<HomePage> getHomePageForCategory(String categorySlug) {
        // If no slug is provided, return the Global (null category) homepage
        if (categorySlug == null || categorySlug.isBlank() || categorySlug.equalsIgnoreCase("global")) {
            return homePageRepository.findByCategoryIsNull();
        }

        // 1. Find the Category by its slug
        return categoryRepository.findBySlug(categorySlug)
                // 2. Use that Category's ID to find the homepage record
                .flatMap(category -> homePageRepository.findByCategoryId(category.getId()));
    }
}