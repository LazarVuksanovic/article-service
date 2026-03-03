package rs.pravda.article_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.pravda.article_service.model.ScheduledPublishing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduledPublishingRepository extends JpaRepository<ScheduledPublishing, UUID> {

    Optional<ScheduledPublishing> findByArticleId(UUID articleId);

    List<ScheduledPublishing> findByScheduledAtLessThanEqual(LocalDateTime now);
}
