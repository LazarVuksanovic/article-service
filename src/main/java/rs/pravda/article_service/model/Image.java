package rs.pravda.article_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image")
@EntityListeners(AuditingEntityListener.class)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String filePath;
    private String filename;
    private String contentType;
    private Long size;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "image_tag",
            joinColumns = @JoinColumn(name = "image_id", foreignKey = @ForeignKey(name = "fk_image_tag_image")),
            inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "fk_image_tag_tag"))
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedBy
    private String createdBy;
}