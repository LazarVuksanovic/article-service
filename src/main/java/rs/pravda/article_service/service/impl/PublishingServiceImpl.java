package rs.pravda.article_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import rs.pravda.article_service.dto.article.ArticlePublishConfig;
import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.model.ScheduledPublishing;
import rs.pravda.article_service.model.homepage.Section;
import rs.pravda.article_service.repository.ArticleRepository;
import rs.pravda.article_service.repository.ScheduledPublishingRepository;
import rs.pravda.article_service.service.PublishingService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishingServiceImpl implements PublishingService {

    private final ArticleRepository articleRepository;
    private final HomePageServiceImpl homePageService;
    private final RevalidationServiceImpl revalidationService;
    private final ScheduledPublishingRepository scheduledRepository;

    @Override
    @Transactional
    public void scheduleArticle(Article article, LocalDateTime scheduledAt, ArticlePublishConfig config) {
        Assert.notNull(article, "Article must not be null");

        scheduledRepository.findByArticleId(article.getId()).ifPresent(scheduledRepository::delete);

        var schedule = ScheduledPublishing.builder()
                .article(article)
                .scheduledAt(scheduledAt)
                .publishConfig(config)
                .build();

        scheduledRepository.save(schedule);
        log.info("Scheduled article {} for {}", article.getId(), scheduledAt);
    }

    @Override
    @Transactional
    @Scheduled(fixedDelay = 60000) // every minute
    public void processDueArticles() {
        var now = LocalDateTime.now();
        var dueTasks = scheduledRepository.findByScheduledAtLessThanEqual(now);

        if (dueTasks.isEmpty()) return;

        dueTasks.forEach(task -> {
            try {
                log.info("Publishing scheduled article {}", task.getArticle().getId());
                publishArticle(task.getArticle(), task.getPublishConfig());
            } catch (Exception e) {
                log.error("Failed to publish scheduled article {}", task.getArticle().getId(), e);
            }
        });

        scheduledRepository.deleteAll(dueTasks);
        log.info("Successfully finished tasks");
    }

    @Override
    public Article publishArticle(Article article, ArticlePublishConfig config) {
        Assert.notNull(article, "Article must not be null");

        article.setPublishedAt(LocalDateTime.now());
        var savedArticle = articleRepository.saveAndFlush(article);
        revalidationService.revalidateArticle(article);

        if (config.pushToCategory()) {
            homePageService.addArticleToLayout(article, article.getCategory(), Section.MAIN, 0);
            revalidationService.revalidateHomePage(article.getCategory());
        }

        if (config.pushToHome()) {
            var section = config.homeSection() != null ? config.homeSection() : Section.MAIN;
            homePageService.addArticleToLayout(article, null, section, 0);
            revalidationService.revalidateHomePage(null);
        }

        return savedArticle;
    }
}