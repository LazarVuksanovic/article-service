package rs.pravda.article_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import rs.pravda.article_service.dto.image.ImageFilterDto;
import rs.pravda.article_service.dto.image.UpdateImageTagsDto;
import rs.pravda.article_service.model.Image;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ImageService {

    Page<Image> getAll(ImageFilterDto filter, Pageable pageable);

    Image upload(MultipartFile file, List<UUID> tagIds) throws IOException;

    void updateTags(UUID imageId, UpdateImageTagsDto dto);

    void delete(UUID id);
}
