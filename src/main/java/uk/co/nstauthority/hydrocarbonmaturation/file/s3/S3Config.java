package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3")
public record S3Config(String accessKey,
                       String secretKey,
                       String endpoint,
                       String regionName,
                       String bucketName,
                       Boolean disableSsl) {
}