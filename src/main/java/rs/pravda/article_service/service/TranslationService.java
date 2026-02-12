package rs.pravda.article_service.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;
import rs.pravda.article_service.localization.TranslatedText;

public interface TranslationService<O, T> {

    T translate (O object);

    static String translateText(@NotNull TranslatedText values) {
        Assert.notNull(values, "Value for translation must not be null");

        return values.get(LocaleContextHolder.getLocale());
    }
}
