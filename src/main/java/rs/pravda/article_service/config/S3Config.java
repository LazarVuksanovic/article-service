package rs.pravda.article_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Profile("prod")
@Configuration
public class S3Config {

    @Value("${application.s3.access-key}")
    private String accessKey;

    @Value("${application.s3.secret-key}")
    private String secretKey;

    @Value("${application.s3.endpoint}")
    private String endpoint;

    @Value("${application.s3.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }

    @ConfigurationProperties(prefix = "application.s3")
    public record S3ConfigProps(
            String accessKey,
            String secretKey,
            String endpoint,
            String region,
            String bucket
    ) {}
}