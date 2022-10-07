package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class S3ClientServiceTest {

  private static final String BUCKET_NAME = "test-bucket";

  @Mock
  private AmazonS3 amazonS3Mock;

  private final S3Config s3Config = new S3Config("", "", "", "", BUCKET_NAME);
  private S3ClientService s3ClientService;
  private String s3Key;


  @BeforeEach
  void setUp() {
    s3ClientService = new S3ClientService(amazonS3Mock, s3Config);
    s3Key = "TEST-KEY";
  }

  @Test
  void getDefaultBucketName() {
    assertThat(s3ClientService.getDefaultBucketName()).isEqualTo(BUCKET_NAME);
  }

  @Test
  void doesBucketExist_exists() {
    when(amazonS3Mock.doesBucketExistV2(BUCKET_NAME)).thenReturn(true);
    assertThat(s3ClientService.doesBucketExist(BUCKET_NAME)).isTrue();
  }

  @Test
  void doesBucketExist_doesntExist() {
    when(amazonS3Mock.doesBucketExistV2(BUCKET_NAME)).thenReturn(false);
    assertThat(s3ClientService.doesBucketExist(BUCKET_NAME)).isFalse();
  }

  @Test
  void doesObjectExist_exists() {
    when(amazonS3Mock.doesObjectExist(BUCKET_NAME, s3Key)).thenReturn(true);
    assertThat(s3ClientService.doesObjectExist(BUCKET_NAME, s3Key)).isTrue();
  }

  @Test
  void doesObjectExist_doesntExist() {
    when(amazonS3Mock.doesObjectExist(BUCKET_NAME, s3Key)).thenReturn(false);
    assertThat(s3ClientService.doesObjectExist(BUCKET_NAME, s3Key)).isFalse();
  }

  @Test
  void storeFile_VerifyInteractions() {
    when(amazonS3Mock.doesBucketExistV2(BUCKET_NAME)).thenReturn(true);
    var inputStream = mock(InputStream.class);
    ObjectMetadata objectMetadata = mock(ObjectMetadata.class);

    s3ClientService.storeFile(BUCKET_NAME, s3Key, inputStream, objectMetadata);

    verify(amazonS3Mock).putObject(BUCKET_NAME, s3Key, inputStream, objectMetadata);
  }

  @Test
  void fetchFile_FileFound() {
    when(amazonS3Mock.doesObjectExist(BUCKET_NAME, s3Key)).thenReturn(true);

    var inputStream = mock(S3ObjectInputStream.class);
    var s3Object = mock(S3Object.class);
    when(s3Object.getObjectContent()).thenReturn(inputStream);
    when(amazonS3Mock.getObject(BUCKET_NAME, s3Key)).thenReturn(s3Object);

    var file = s3ClientService.fetchFile(BUCKET_NAME, s3Key);

    assertThat(file).isEqualTo(inputStream);
  }

  @Test
  void fetchFile_FileNotFound() {
    when(amazonS3Mock.doesObjectExist(BUCKET_NAME, s3Key)).thenReturn(false);

    assertThatExceptionOfType(S3ObjectNotFoundException.class).isThrownBy(() -> {
      var file = s3ClientService.fetchFile(BUCKET_NAME, s3Key);
    });
  }

  @Test
  void deleteFile_FileFound() {
    when(amazonS3Mock.doesObjectExist(BUCKET_NAME, s3Key)).thenReturn(true);

    s3ClientService.deleteFile(BUCKET_NAME, s3Key);
    verify(amazonS3Mock).deleteObject(BUCKET_NAME, s3Key);
  }

  @Test
  void deleteFile_FileNotFound() {
    when(amazonS3Mock.doesObjectExist(BUCKET_NAME, s3Key)).thenReturn(false);

    assertThatExceptionOfType(S3ObjectNotFoundException.class).isThrownBy(() -> {
      s3ClientService.deleteFile(BUCKET_NAME, s3Key);
    });
  }

  @Test
  void deleteFile_OperationFailed() {
    when(amazonS3Mock.doesObjectExist(BUCKET_NAME, s3Key)).thenReturn(true);
    doThrow(SdkClientException.class).when(amazonS3Mock).deleteObject(BUCKET_NAME, s3Key);

    assertThatExceptionOfType(S3DeleteFileFailedException.class).isThrownBy(() -> {
      s3ClientService.deleteFile(BUCKET_NAME, s3Key);
    });
  }
}