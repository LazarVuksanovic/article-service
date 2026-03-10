package rs.pravda.article_service.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import rs.pravda.article_service.dto.image.ImageFilterDto;
import rs.pravda.article_service.model.Image;
import rs.pravda.article_service.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class ImageSpecification {

    public static Specification<Image> filterImages(ImageFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.tagIds() != null && !filter.tagIds().isEmpty()) {
                Join<Image, Tag> tagsJoin = root.join("tags");
                predicates.add(tagsJoin.get("id").in(filter.tagIds()));

                query.distinct(true);
            }

            if (filter.filenameQuery() != null && !filter.filenameQuery().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("filename")), "%" + filter.filenameQuery().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}