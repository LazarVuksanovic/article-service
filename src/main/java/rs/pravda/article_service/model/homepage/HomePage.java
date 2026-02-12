package rs.pravda.article_service.model.homepage;

import jakarta.persistence.*;
import lombok.*;
import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "home_page")
public class HomePage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(name = "home_page_nav_categories")
    @OrderColumn(name = "priority")
    private List<Category> navCategories = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "home_page_main_articles")
    @OrderColumn(name = "priority")
    private List<Article> main = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "home_page_side_articles")
    @OrderColumn(name = "priority")
    private List<Article> side = new ArrayList<>();
}