package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("production")
class S3ClientProductionProvider {

  @Bean
  AmazonS3 s3Client(S3Config s3Config) {

    return S3Utils.getAmazonS3ClientBuilder(s3Config)
        .build();
  }
}
