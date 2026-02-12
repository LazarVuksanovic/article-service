package rs.pravda.article_service.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;

public class SpecificationUtils {

    public static Expression<String> jsonbText(CriteriaBuilder cb, Path<?> jsonbPath, String lang) {
        return cb.function("jsonb_extract_path_text", String.class, jsonbPath, cb.literal(lang));
    }
}
