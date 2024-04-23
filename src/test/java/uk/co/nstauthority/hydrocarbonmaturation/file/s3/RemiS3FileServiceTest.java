package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.s3.S3Exception;
import uk.co.fivium.fileuploadlibrary.s3.S3FileService;

@ExtendWith(MockitoExtension.class)
class RemiS3FileServiceTest {
  @Mock
  private AmazonS3 amazonS3;
  @Mock
  private S3FileService s3FileService;
  @InjectMocks
  private RemiS3FileService underTest;
  private final String PATH_PREFIX = "path/to/files/";
  private final String S3_BUCKET = "bucket";
  private final String KEY = "testKey";
  public static final List<S3ObjectSummary> OBJECT_SUMMARIES = List.of(
      createS3ObjectSummary("file1", 100L),
      createS3ObjectSummary("file2", 200L),
      createS3ObjectSummary("file3", 300L)
  );
  private final String CONTINUATION_TOKEN = "token";

  private static S3ObjectSummary createS3ObjectSummary(String key, long size) {
    var objectSummary = new S3ObjectSummary();
    objectSummary.setKey(key);
    objectSummary.setSize(size);
    return objectSummary;
  }

  @Test
  void listS3Files_returnsListOfS3Files() throws S3Exception {
    var expectedFiles = OBJECT_SUMMARIES.stream()
        .map(S3File::from)
        .toList();

    var listObjectsV2Result = mockListObjectsV2Result(false, null);
    doReturn(listObjectsV2Result).when(amazonS3).listObjectsV2(any(ListObjectsV2Request.class));

    var actualFiles = underTest.listS3Files(S3_BUCKET, PATH_PREFIX, CONTINUATION_TOKEN);

    assertThat(actualFiles)
        .isEqualTo(new ListS3FilesResult(false, null, expectedFiles));
  }

  private static ListObjectsV2Result mockListObjectsV2Result(boolean truncated, String continuationToken) {
    var listObjectsV2Result = mock(ListObjectsV2Result.class);
    when(listObjectsV2Result.getObjectSummaries())
        .thenReturn(OBJECT_SUMMARIES);
    when(listObjectsV2Result.isTruncated())
        .thenReturn(truncated);
    when(listObjectsV2Result.getNextContinuationToken())
        .thenReturn(continuationToken);
    return listObjectsV2Result;
  }

  @Test
  void listS3Files_requestContainsContinuationToken() throws S3Exception {
    var listObjectsV2Result = mockListObjectsV2Result(false, null);
    doReturn(listObjectsV2Result).when(amazonS3).listObjectsV2(any(ListObjectsV2Request.class));

    var argumentCaptor = ArgumentCaptor.forClass(ListObjectsV2Request.class);
    var result = underTest.listS3Files(S3_BUCKET, PATH_PREFIX, CONTINUATION_TOKEN);

    verify(amazonS3).listObjectsV2(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue())
        .extracting(
            ListObjectsV2Request::getContinuationToken,
            ListObjectsV2Request::getPrefix,
            ListObjectsV2Request::getBucketName
        )
        .containsExactly(
            CONTINUATION_TOKEN,
            PATH_PREFIX,
            S3_BUCKET
        );
  }

  @Test
  void listS3Files_requestDoesNotContainContinuationToken() throws S3Exception {
    var listObjectsV2Result = mockListObjectsV2Result(false, null);
    doReturn(listObjectsV2Result).when(amazonS3).listObjectsV2(any(ListObjectsV2Request.class));

    var argumentCaptor = ArgumentCaptor.forClass(ListObjectsV2Request.class);
    var result = underTest.listS3Files(S3_BUCKET, PATH_PREFIX, null);

    verify(amazonS3).listObjectsV2(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue())
        .extracting(
            ListObjectsV2Request::getContinuationToken,
            ListObjectsV2Request::getPrefix,
            ListObjectsV2Request::getBucketName
        )
        .containsExactly(
            null,
            PATH_PREFIX,
            S3_BUCKET
        );
  }

  @Test
  void listS3Files_handlesAmazonS3Exception() {
    when(amazonS3.listObjectsV2(any(ListObjectsV2Request.class)))
        .thenThrow(new AmazonClientException("Error occurred"));

    assertThatThrownBy(() -> underTest.listS3Files(S3_BUCKET, PATH_PREFIX, CONTINUATION_TOKEN))
        .isInstanceOf(S3Exception.class)
        .hasMessageStartingWith("Failed to retrieve S3 files");
  }

  @Test
  void downloadFile_returnsInputStream() throws S3Exception {
    var inputStream = mock(InputStream.class);
    when(s3FileService.downloadFile(S3_BUCKET, KEY)).thenReturn(inputStream);
    var result = underTest.downloadFile(S3_BUCKET, KEY);
    assertThat(result).isSameAs(inputStream);
  }
}