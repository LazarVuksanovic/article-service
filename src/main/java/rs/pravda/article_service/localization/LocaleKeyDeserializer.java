package rs.pravda.article_service.localization;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.util.Locale;

public class LocaleKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctx) {
        if (key == null || key.isBlank()) return null;
        return Locale.forLanguageTag(key);
    }
}

