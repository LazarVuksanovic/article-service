package rs.pravda.article_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rs.pravda.article_service.dto.image.ImageFilterDto;
import rs.pravda.article_service.dto.image.UpdateImageTagsDto;
import rs.pravda.article_service.exception.EntityNotFoundException;
import rs.pravda.article_service.model.Image;
import rs.pravda.article_service.repository.ImageRepository;
import rs.pravda.article_service.repository.TagRepository;
import rs.pravda.article_service.repository.specification.ImageSpecification;
import rs.pravda.article_service.service.ImageService;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final S3Client s3Client;
    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;

    @Value("${application.s3.bucket}")
    private String bucket;

    @Value("${application.imgproxy.url}")
    private String imgProxyBaseUrl;

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Override
    @Transactional(readOnly = true)
    public Page<Image> getAll(ImageFilterDto filter, Pageable pageable) {
        return imageRepository.findAll(
                ImageSpecification.filterImages(filter),
                pageable
        );
    }

    @Override
    public Image upload(MultipartFile file, List<UUID> tagIds) throws IOException {
        var originalFilename = file.getOriginalFilename();
        var path = generateUniquePath(originalFilename);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(path)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        var imageBuilder = Image.builder()
                .filePath(path)
                .filename(originalFilename)
                .contentType(file.getContentType())
                .size(file.getSize());

        if (tagIds != null && !tagIds.isEmpty()) {
            var tags = new HashSet<>(tagRepository.findAllById(tagIds));
            imageBuilder.tags(tags);
        }

        return imageRepository.save(imageBuilder.build());
    }

    @Override
    @Transactional
    public void updateTags(UUID imageId, UpdateImageTagsDto dto) {
        var image = imageRepository.findById(imageId).orElseThrow(() -> new EntityNotFoundException("Image"));

        if (dto.tagIds() != null) {
            var tags = new HashSet<>(tagRepository.findAllById(dto.tagIds()));
            image.setTags(tags);
            imageRepository.save(image);
        }
    }

    @Override
    public void delete(UUID id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Image"));

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(image.getFilePath())
                .build()

        );
        imageRepository.delete(image);
    }

    private String generateUniquePath(String filename) {
        String ext = "";
        String name = filename;
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex >= 0) {
            ext = filename.substring(dotIndex);
            name = filename.substring(0, dotIndex);
        }

        var slug = toSlug(name);
        var shortHash = UUID.randomUUID().toString().substring(0, 8);

        return "pravda/" + slug + "-" + shortHash + ext.toLowerCase();
    }

    private String toSlug(String input) {
        if (input == null) return "";

        var nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        var normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        var slug = normalized.replaceAll("[^\\p{ASCII}]", "");
        slug = NON_LATIN.matcher(slug).replaceAll("");

        return slug.toLowerCase();
    }
}
