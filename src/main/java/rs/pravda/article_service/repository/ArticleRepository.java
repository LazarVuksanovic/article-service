package rs.pravda.article_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import rs.pravda.article_service.model.Article;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, JpaSpecificationExecutor<Article> {

    Optional<Article> findBySlug(String slug);

    List<Article> findByIdIn(List<UUID> articleIds);

    boolean existsBySlug(String slug);
}
