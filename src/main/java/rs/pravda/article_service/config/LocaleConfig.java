package rs.pravda.article_service.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class LocaleConfig {

    private final LocaleProperties localeProperties;

    @Bean
    public LocaleResolver localeResolver() {
        var fallback = (Locale.forLanguageTag(localeProperties.defaultLocale()));
        var supported = localeProperties.supported().stream()
                .map(Locale::forLanguageTag)
                .toList();

        return new AcceptHeaderLocaleResolver() {
            { setDefaultLocale(fallback); setSupportedLocales(supported); }

            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                var al = request.getHeader("Accept-Language");

                if (al == null || al.isBlank())
                    return fallback;

                return super.resolveLocale(request);
            }
        };
    }

    @ConfigurationProperties(prefix = "locale")
    public record LocaleProperties(
            String defaultLocale,
            List<String> supported
    ) {}
}
