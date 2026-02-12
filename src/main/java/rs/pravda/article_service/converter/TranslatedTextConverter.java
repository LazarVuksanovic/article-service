package rs.pravda.article_service.converter;

import jakarta.persistence.Converter;
import rs.pravda.article_service.localization.TranslatedText;

@Converter
public class TranslatedTextConverter extends JsonConverter<TranslatedText>{
}
