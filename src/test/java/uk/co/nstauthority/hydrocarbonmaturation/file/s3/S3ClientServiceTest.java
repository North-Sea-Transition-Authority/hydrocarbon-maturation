package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class S3ClientServiceTest {

  private static final String BUCKET_NAME = "test-bucket";

  @Mock
  private AmazonS3 amazonS3Mock;

  private final S3Config s3Config = new S3Config("", "", "", "", BUCKET_NAME);
  private S3ClientService s3ClientService;


  @BeforeEach
  void setUp() {
    s3ClientService = new S3ClientService(amazonS3Mock, s3Config);
  }

  @Test
  public void testGetBucketName() {
    assertThat(s3ClientService.getDefaultBucketName()).isEqualTo(BUCKET_NAME);
  }

  @Test
  public void testDoesBucketExist_exists() {
    when(amazonS3Mock.doesBucketExistV2(BUCKET_NAME)).thenReturn(true);
    assertThat(s3ClientService.doesBucketExist()).isTrue();
  }

  @Test
  public void testDoesBucketExist_doesntExist() {
    when(amazonS3Mock.doesBucketExistV2(BUCKET_NAME)).thenReturn(false);
    assertThat(s3ClientService.doesBucketExist()).isFalse();
  }
}