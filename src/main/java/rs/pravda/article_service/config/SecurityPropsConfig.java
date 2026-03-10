package rs.pravda.article_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SecurityPropsConfig {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Value("${application.security.jwt.cookie.secure}")
    private boolean secureCookies;

    @Value("${application.security.jwt.cookie.domain:#{null}}")
    private String cookieDomain;

    @Value("${application.security.google.client-id}")
    private String googleClientId;
}
