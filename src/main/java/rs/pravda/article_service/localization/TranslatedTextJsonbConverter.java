package rs.pravda.article_service.localization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Converter(autoApply = true)
public class TranslatedTextJsonbConverter implements AttributeConverter<TranslatedText, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    private static final TypeReference<Map<String,String>> STRING_MAP = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(TranslatedText attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return "{}";
            }
            Map<String,String> asStringKeys = new LinkedHashMap<>();
            attribute.forEach((loc, text) -> {
                if (loc != null && text != null) {
                    asStringKeys.put(loc.toLanguageTag(), text);
                }
            });
            return MAPPER.writeValueAsString(asStringKeys);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not serialize TranslatedText", e);
        }
    }

    @Override
    public TranslatedText convertToEntityAttribute(String dbData) {
        try {
            TranslatedText result = TranslatedText.sorted();
            if (dbData == null || dbData.isBlank() || "{}".equals(dbData.trim())) {
                return result;
            }
            Map<String,String> raw = MAPPER.readValue(dbData, STRING_MAP);
            raw.forEach((langTag, text) -> {
                if (langTag != null && text != null) {
                    result.put(Locale.forLanguageTag(langTag), text);
                }
            });
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not deserialize TranslatedText", e);
        }
    }
}
