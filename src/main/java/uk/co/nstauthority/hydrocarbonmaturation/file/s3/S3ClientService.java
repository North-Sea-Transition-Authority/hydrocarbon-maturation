package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import com.amazonaws.services.s3.AmazonS3;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;
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

  String getDefaultBucketName() {
    return s3Config.getDefaultBucketName();
  }

  boolean doesBucketExist() {
    return s3Client.doesBucketExistV2(getDefaultBucketName());
  }

  boolean doesObjectExist(String bucketName, String s3Key) {
    return s3Client.doesObjectExist(bucketName, s3Key);
  }

  public String storeFile(S3Folder s3Folder, File file) {
    String s3Key = createNewS3Key(s3Folder);
    s3Client.putObject(getDefaultBucketName(), s3Key, file);

    return s3Key;
  }

  public InputStream fetchFile(String s3Key) {
    String defaultBucketName = getDefaultBucketName();
    doesObjectExistOrThrow(defaultBucketName, s3Key);

    return s3Client.getObject(defaultBucketName, s3Key).getObjectContent();
  }

  public void deleteFile(String s3Key) {
    var bucketName = getDefaultBucketName();

    doesObjectExistOrThrow(bucketName, s3Key);

    s3Client.deleteObject(bucketName, s3Key);
  }

  private String createNewS3Key(S3Folder s3Folder) {
    return s3Folder + "/" + UUID.randomUUID();
  }

  private void doesObjectExistOrThrow(String bucketName, String s3Key) {
    if (!doesObjectExist(bucketName, s3Key)) {
      throw new IllegalStateException("Could not find file stored with path " + s3Key);
    }
  }

}