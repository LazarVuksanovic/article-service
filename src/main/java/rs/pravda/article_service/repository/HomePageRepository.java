package rs.pravda.article_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.pravda.article_service.model.homepage.HomePage;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HomePageRepository  extends JpaRepository<HomePage, UUID> {

    Optional<HomePage> findByCategoryIsNull();

    Optional<HomePage> findByCategoryId(UUID categoryId);
}
