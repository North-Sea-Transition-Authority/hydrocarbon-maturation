package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ClientService;

@ExtendWith(MockitoExtension.class)
class FileUploadStorageServiceTest {

  @Mock
  private S3ClientService s3ClientService;

  @InjectMocks
  private FileUploadStorageService fileUploadStorageService;

  @Mock
  private InputStream inputStream;
  private static final UploadedFile uploadedFile = FileTestUtil.createValidUploadedFile();

  @Test
  void uploadFile() {
    fileUploadStorageService.uploadFile(inputStream, uploadedFile);
    verify(s3ClientService).storeFile(eq(FileTestUtil.VALID_BUCKET_NAME), anyString(), any(InputStream.class), any(ObjectMetadata.class));
  }

  @Test
  void deleteFile_FileDeleted() {
    fileUploadStorageService.deleteFile(uploadedFile);

    verify(s3ClientService).deleteFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());
  }

  @Test
  void downloadFile_VerifyInteractions() {
    fileUploadStorageService.downloadFile(uploadedFile);
    verify(s3ClientService).fetchFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());
  }
}