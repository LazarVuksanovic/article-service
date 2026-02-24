package rs.pravda.article_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.pravda.article_service.model.Category;
import rs.pravda.article_service.model.homepage.ArticleSection;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleSectionRepository extends JpaRepository<ArticleSection, UUID> {

    @Query("SELECT s FROM ArticleSection s WHERE " +
            "(:category IS NULL AND s.category IS NULL) OR " +
            "(:category IS NOT NULL AND s.category = :category) " +
            "ORDER BY s.order ASC")
    List<ArticleSection> findByCategory(@Param("category") Category category);

    List<ArticleSection> findByArticleId(UUID articleId);

    void deleteByCategory(Category category);
}