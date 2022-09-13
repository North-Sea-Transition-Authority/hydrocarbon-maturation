package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s3")
class S3Config {

  private String accessKey;
  private String secretKey;
  private String endpoint;
  private String regionName;
  private String bucketName;

  String getAccessKey() {
    return accessKey;
  }

  void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  String getSecretKey() {
    return secretKey;
  }

  void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  String getEndpoint() {
    return endpoint;
  }

  void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  String getRegionName() {
    return regionName;
  }

  void setRegionName(String regionName) {
    this.regionName = regionName;
  }

  String getDefaultBucketName() {
    return bucketName;
  }

  void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }
}