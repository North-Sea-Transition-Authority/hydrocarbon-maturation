package uk.co.nstauthority.hydrocarbonmaturation.file.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.s3.S3Exception;
import uk.co.fivium.fileuploadlibrary.s3.S3FileService;

@Service
public class RemiS3FileService {
  private final AmazonS3 amazonS3;
  private final S3FileService s3FileService;

  @Autowired
  RemiS3FileService(AmazonS3 amazonS3, S3FileService s3FileService) {
    this.amazonS3 = amazonS3;
    this.s3FileService = s3FileService;
  }

  public List<S3File> listS3Files(String bucket, String prefix) throws S3Exception {
    try {
      return amazonS3.listObjectsV2(bucket, prefix).getObjectSummaries().stream()
          .map(S3File::from)
          .toList();
    } catch (AmazonClientException e) {
      throw new S3Exception("Failed to retrieve S3 files", e);
    }
  }

  public InputStream downloadFile(String bucketName, String key) throws S3Exception {
    return s3FileService.downloadFile(bucketName, key);
  }
}
