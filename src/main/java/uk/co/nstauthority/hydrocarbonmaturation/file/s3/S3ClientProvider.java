package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import com.amazonaws.auth.AWSCredentialsProvider;
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
    BasicAWSCredentials credentials = new BasicAWSCredentials(s3Config.getAccessKey(),
        s3Config.getSecretKey());

    AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

    AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
        s3Config.getEndpoint(), s3Config.getRegionName());

    return AmazonS3ClientBuilder
        .standard()
        .withEndpointConfiguration(endpointConfiguration)
        .withPathStyleAccessEnabled(true)
        .withCredentials(credentialsProvider)
        .build();
  }
}
