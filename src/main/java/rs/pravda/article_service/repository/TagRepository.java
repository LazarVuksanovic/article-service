package rs.pravda.article_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.pravda.article_service.model.Tag;

import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    @Query(value = """
        select exists(
          select 1
          from tag t
          where lower(trim(t.name ->> :lang)) = lower(trim(:value))
        )
        """, nativeQuery = true)
    boolean existsNameTranslation(
            @Param("lang") String langTag,
            @Param("value") String value
    );

    @Query(value = """
        select exists(
          select 1
          from tag t
          where t.id <> :excludeId
            and lower(trim(t.name ->> :lang)) = lower(trim(:value))
        )
        """, nativeQuery = true)
    boolean existsByNameInLocaleExcludingId(
            @Param("lang") String langTag,
            @Param("value") String value,
            @Param("excludeId") UUID excludeId
    );
}
