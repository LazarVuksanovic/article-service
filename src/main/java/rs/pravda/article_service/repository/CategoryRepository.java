package rs.pravda.article_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.pravda.article_service.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {

    List<Category> findByParentCategoryId(UUID parentCategoryId);

    @Query(value = """
    SELECT c.* FROM category c
    WHERE EXISTS (
        SELECT 1 FROM jsonb_each_text(c.name) AS x(lang, val)
        WHERE lower(trim(x.val)) = lower(trim(:name))
    )
    LIMIT 1
    """, nativeQuery = true)
    Optional<Category> findByNameValue(@Param("name") String name);

    @Query(value = """
        select exists(
          select 1
          from category c
          where lower(trim(c.name ->> :lang)) = lower(trim(:value))
        )
        """, nativeQuery = true)
    boolean existsNameTranslation(
            @Param("lang") String langTag,
            @Param("value") String value
    );

    @Query(value = """
        select exists(
          select 1
          from category c
          where c.id <> :excludeId
            and lower(trim(c.name ->> :lang)) = lower(trim(:value))
        )
        """, nativeQuery = true)
    boolean existsByNameInLocaleExcludingId(
            @Param("lang") String langTag,
            @Param("value") String value,
            @Param("excludeId") UUID excludeId
    );

    Optional<Category> findBySlug(String slug);
}
