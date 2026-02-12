package rs.pravda.article_service.localization;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Comparator;
import java.util.Locale;
import java.util.TreeMap;

@JsonSerialize(keyUsing = LocaleKeySerializer.class)
@JsonDeserialize(keyUsing = LocaleKeyDeserializer.class)
public class TranslatedText extends TreeMap<Locale, String> {

    public TranslatedText() {
        super(Comparator.comparing(Locale::toLanguageTag));
    }

    public TranslatedText(Comparator<Locale> comparator) {
        super(comparator);
    }

    public static TranslatedText sorted() {
        return new TranslatedText();
    }

    @Override
    public String toString() {
        return this.entrySet().stream()
                .map(e -> e.getKey().toLanguageTag() + "=" + e.getValue())
                .collect(java.util.stream.Collectors.joining(", ", "{", "}"));
    }
}
