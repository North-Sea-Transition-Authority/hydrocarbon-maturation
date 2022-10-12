package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class S3ClientProvider {

  @Bean
  AmazonS3 s3Client(S3Config s3Config) {

    var credentials = new BasicAWSCredentials(s3Config.accessKey(), s3Config.secretKey());

    var credentialsProvider = new AWSStaticCredentialsProvider(credentials);

    var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(s3Config.endpoint(), s3Config.regionName());

    AmazonS3ClientBuilder amazonS3ClientBuilder = AmazonS3ClientBuilder
        .standard()
        .withEndpointConfiguration(endpointConfiguration)
        .withPathStyleAccessEnabled(true)
        .withCredentials(credentialsProvider);

    if (s3Config.disableSsl()) {
      amazonS3ClientBuilder.withClientConfiguration(new ClientConfiguration().withProtocol(Protocol.HTTP));
    }

    return amazonS3ClientBuilder
        .build();
  }
}
