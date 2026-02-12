package rs.pravda.article_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.pravda.article_service.dto.tag.CreateTagDto;
import rs.pravda.article_service.dto.tag.TagDto;
import rs.pravda.article_service.dto.tag.TagsFilterDto;
import rs.pravda.article_service.dto.tag.TranslatedTag;
import rs.pravda.article_service.dto.tag.UpdateTagDto;
import rs.pravda.article_service.service.TagService;

import java.util.List;
import java.util.UUID;

import static rs.pravda.article_service.dto.tag.TagDto.toTagDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    List<TagDto> getTags(
            @RequestParam(required = false) String query
    ) {
        var filter = TagsFilterDto.builder()
                .query(query)
                .build();

        return tagService.getTags(filter)
                .stream()
                .map(TagDto::toTagDto)
                .toList();
    }

    @GetMapping("/{id}")
    TranslatedTag getTranslatedTag(@PathVariable UUID id) {
        return tagService.getTranslatedTag(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TagDto createTag(@RequestBody @Valid CreateTagDto createTagDto) {
        return toTagDto(tagService.createTag(createTagDto));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateTag(@PathVariable UUID id,
                   @RequestBody @Valid UpdateTagDto updateTagDto) {
        tagService.updateTag(id, updateTagDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
    }
}
