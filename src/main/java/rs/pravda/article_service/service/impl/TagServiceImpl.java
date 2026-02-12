package rs.pravda.article_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import rs.pravda.article_service.dto.tag.CreateTagDto;
import rs.pravda.article_service.dto.tag.TagsFilterDto;
import rs.pravda.article_service.dto.tag.TranslatedTag;
import rs.pravda.article_service.dto.tag.UpdateTagDto;
import rs.pravda.article_service.exception.EntityAlreadyExists;
import rs.pravda.article_service.exception.EntityNotFoundException;
import rs.pravda.article_service.model.Tag;
import rs.pravda.article_service.repository.TagRepository;
import rs.pravda.article_service.service.TagService;

import java.util.*;

import static rs.pravda.article_service.service.TranslationService.translateText;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public Optional<Tag> getTag(UUID id) {
        Assert.notNull(id, "Tag id must not be null");

        return tagRepository.findById(id);
    }

    @Override
    public TranslatedTag getTranslatedTag(UUID id) {
        return getTag(id)
                .map(this::translate)
                .orElseThrow(() -> new EntityNotFoundException("Tag"));
    }

    @Override
    public TranslatedTag getTranslatedTag(Tag tag) {
        Assert.notNull(tag, "Tag must not be null");

        return translate(tag);
    }

    @Override
    public Tag createTag(CreateTagDto createTagDto) {
        createTagDto.name().forEach((lang, name) -> {
            if (name == null || name.trim().isEmpty())
                throw new IllegalArgumentException(
                        "Tag name translation for language [" + lang + "] cannot be null or empty"
                );

            if (tagRepository.existsNameTranslation(lang.toLanguageTag(), name))
                throw new EntityAlreadyExists("Tag translation [" + lang + "]");
        });

        var newTag = Tag.builder()
                .name(createTagDto.name())
                .build();

        return tagRepository.save(newTag);
    }

    @Override
    public void updateTag(UUID id, UpdateTagDto updateTagDto) {
        var tag = getTag(id).orElseThrow(() -> new EntityNotFoundException("Tag"));

        updateTagDto.name().forEach((lang, name) -> {
            if (name == null || name.trim().isEmpty())
                throw new IllegalArgumentException(
                        "Tag name translation for language [" + lang + "] cannot be null or empty"
                );

            if (tagRepository.existsByNameInLocaleExcludingId(lang.toLanguageTag(), name, id))
                throw new EntityAlreadyExists("Tag translation [" + lang + "]");
        });

        tag.setName(updateTagDto.name());

        tagRepository.save(tag);
    }

    @Override
    public void deleteTag(UUID id) {
        Assert.notNull(id, "Tag id must not be null");

        tagRepository.deleteById(id);
    }

    @Override
    public List<Tag> getTags(TagsFilterDto filterDto) {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> getTags(List<UUID> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return List.of();

        return tagRepository.findAllById(tagIds);
    }

    @Override
    public TranslatedTag translate(Tag tag) {
        return TranslatedTag.builder()
                .id(tag.getId())
                .name(translateText(tag.getName()))
                .build();
    }
}
