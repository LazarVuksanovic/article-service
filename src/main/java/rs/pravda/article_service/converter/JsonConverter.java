package rs.pravda.article_service.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class JsonConverter<T> implements AttributeConverter<T, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public String convertToDatabaseColumn(T attribute) {
        try {
            return attribute != null ? OBJECT_MAPPER.writeValueAsString(attribute) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string", e);
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        try {
            return StringUtils.hasText(dbData) ? OBJECT_MAPPER.readValue(dbData, getTypeReference()) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON string to object", e);
        }
    }

    protected TypeReference<T> getTypeReference() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0) {
                Type actualType = typeArguments[0];
                return new TypeReference<T>() {
                    @Override
                    public Type getType() {
                        return actualType;
                    }
                };
            }
        }
        throw new RuntimeException("Extension of JsonConverter requires generic type T to be specified");
    }
}
