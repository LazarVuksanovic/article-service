package rs.pravda.article_service.service;

import rs.pravda.article_service.dto.tag.CreateTagDto;
import rs.pravda.article_service.dto.tag.TagsFilterDto;
import rs.pravda.article_service.dto.tag.TranslatedTag;
import rs.pravda.article_service.dto.tag.UpdateTagDto;
import rs.pravda.article_service.model.Tag;

import java.util.*;

public interface TagService extends TranslationService<Tag, TranslatedTag> {

    List<Tag> getTags(TagsFilterDto filterDto);

    List<Tag> getTags(List<UUID> tagIds);

    Optional<Tag> getTag(UUID id);

    TranslatedTag getTranslatedTag(UUID id);

    TranslatedTag getTranslatedTag(Tag tag);

    Tag createTag(CreateTagDto createTagDto);

    void updateTag(UUID id, UpdateTagDto updateTagDto);

    void deleteTag(UUID id);
}
