package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class S3ClientService {

  private final AmazonS3 s3Client;
  private final S3Config s3Config;

  @Autowired
  public S3ClientService(AmazonS3 s3Client, S3Config s3Config) {
    this.s3Client = s3Client;
    this.s3Config = s3Config;
  }

  public String getDefaultBucketName() {
    return s3Config.getDefaultBucketName();
  }

  boolean doesBucketExist() {
    return s3Client.doesBucketExistV2(getDefaultBucketName());
  }

  boolean doesObjectExist(String bucketName, String s3Key) {
    return s3Client.doesObjectExist(bucketName, s3Key);
  }

  public void storeFile(String s3Key, InputStream fileInputStream, ObjectMetadata objectMetadata) {
    s3Client.putObject(getDefaultBucketName(), s3Key, fileInputStream, objectMetadata);
  }

  public InputStream fetchFile(String s3Key) {
    var defaultBucketName = getDefaultBucketName();
    doesObjectExistOrThrow(defaultBucketName, s3Key);

    return s3Client.getObject(defaultBucketName, s3Key).getObjectContent();
  }

  public void deleteFile(String s3Key) {
    var bucketName = getDefaultBucketName();

    doesObjectExistOrThrow(bucketName, s3Key);

    try {
      s3Client.deleteObject(bucketName, s3Key);
    } catch (SdkClientException e) {
      throw new S3DeleteFileFailedException(bucketName, s3Key, e);
    }
  }

  private void doesObjectExistOrThrow(String bucketName, String s3Key) {
    if (!doesObjectExist(bucketName, s3Key)) {
      throw new S3ObjectNotFoundException(bucketName, s3Key);
    }
  }
}