package rs.pravda.article_service.model.homepage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.pravda.article_service.model.Article;
import rs.pravda.article_service.model.Category;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "article_section")
public class ArticleSection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Section section;

    @Column(name = "sort_order", nullable = false)
    private Integer order;

    public static ArticleSection increaseOrder(ArticleSection articleSection){
        articleSection.setOrder(articleSection.getOrder() + 1);
        return articleSection;
    }
}