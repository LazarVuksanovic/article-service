package rs.pravda.article_service.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import rs.pravda.article_service.dto.article.ArticledFilterDto;
import rs.pravda.article_service.model.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleSpecification {

    public static Specification<Article> createSpecification(ArticledFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) return cb.conjunction();

            // 1. Title Search (JSONB Optimization)
            if (filter.query() != null && !filter.query().isBlank()) {
                predicates.add(titleContainsPredicate(root, cb, filter.query()));
            }

            // 2. Visibility Logic (publishedAt)
            // If showHidden is false, we ONLY show articles where publishedAt is NOT NULL
            if (!filter.showHidden()) {
                predicates.add(cb.isNotNull(root.get("publishedAt")));
            }

            // 3. Archived Logic
            if (!filter.showArchived()) {
                predicates.add(cb.isFalse(root.get("archived")));
            }

            // 4. Category Filter
            if (filter.categoryIds() != null && !filter.categoryIds().isEmpty()) {
                predicates.add(root.get("category").get("id").in(filter.categoryIds()));
            }

            // 5. Created Date Range
            if (filter.from() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.from()));
            }
            if (filter.to() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.to()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate titleContainsPredicate(Root<Article> root, CriteriaBuilder cb, String query) {
        String searchTerm = query.trim().toLowerCase();

        // Detect if the query contains Cyrillic characters
        // Unicode range for Cyrillic is \u0400-\u04FF
        boolean isCyrillic = searchTerm.matches(".*[\\u0400-\\u04FF].*");
        String targetKey = isCyrillic ? "sr-Cyrl" : "sr-Latn";

        // Postgres function: jsonb_extract_path_text(title, 'sr-Cyrl')
        // This is equivalent to title ->> 'sr-Cyrl'
        Expression<String> targetedTitle = cb.function(
                "jsonb_extract_path_text",
                String.class,
                root.get("title"),
                cb.literal(targetKey)
        );

        return cb.like(cb.lower(targetedTitle), "%" + searchTerm + "%");
    }
}