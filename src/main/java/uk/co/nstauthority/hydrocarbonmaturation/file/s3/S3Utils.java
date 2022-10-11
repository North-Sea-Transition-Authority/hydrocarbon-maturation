package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Utils {

  private S3Utils() {
  }

  public static AmazonS3ClientBuilder getAmazonS3ClientBuilder(S3Config s3Config) {
    var credentials = new BasicAWSCredentials(s3Config.accessKey(), s3Config.secretKey());

    var credentialsProvider = new AWSStaticCredentialsProvider(credentials);

    var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(s3Config.endpoint(), s3Config.regionName());

    return AmazonS3ClientBuilder
        .standard()
        .withEndpointConfiguration(endpointConfiguration)
        .withPathStyleAccessEnabled(true)
        .withCredentials(credentialsProvider);
  }
}
