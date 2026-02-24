package rs.pravda.article_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rs.pravda.article_service.dto.image.ImageFilterDto;
import rs.pravda.article_service.dto.image.UpdateImageTagsDto;
import rs.pravda.article_service.model.Image;
import rs.pravda.article_service.service.ImageService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class DevImageServiceImpl implements ImageService {
    @Override
    public Page<Image> getAll(ImageFilterDto filter, Pageable pageable) {
        return null;
    }

    @Override
    public Image upload(MultipartFile file, List<UUID> tagIds) throws IOException {
        return null;
    }

    @Override
    public void updateTags(UUID imageId, UpdateImageTagsDto dto) {

    }

    @Override
    public void delete(UUID id) {

    }
}
