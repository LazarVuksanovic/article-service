package rs.pravda.article_service.repository.specification;

import jakarta.persistence.criteria.Expression;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import rs.pravda.article_service.model.Category;

@NoArgsConstructor
public class CategorySpecification {

    public static Specification<Category> nameContainsLang(String lang, String term) {
        return (root, q, cb) -> {
            if (lang == null || term == null || term.isBlank()) return cb.conjunction();
            Expression<String> nameLang = SpecificationUtils.jsonbText(cb, root.get("name"), lang);
            return cb.like(cb.lower(nameLang), "%" + term.toLowerCase() + "%");
        };
    }
}
