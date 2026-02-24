package rs.pravda.article_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.pravda.article_service.dto.image.ImageFilterDto;
import rs.pravda.article_service.dto.image.UpdateImageTagsDto;
import rs.pravda.article_service.model.Image;
import rs.pravda.article_service.service.ImageService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping
    public Page<Image> getImages(
            @ModelAttribute ImageFilterDto filter,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return imageService.getAll(filter, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Image upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "tagIds", required = false) List<UUID> tagIds
    ) throws IOException {
        return imageService.upload(file, tagIds);
    }

    @PutMapping("/{id}/tags")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTags(@PathVariable UUID id, @RequestBody UpdateImageTagsDto dto) {
        imageService.updateTags(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        imageService.delete(id);
    }
}