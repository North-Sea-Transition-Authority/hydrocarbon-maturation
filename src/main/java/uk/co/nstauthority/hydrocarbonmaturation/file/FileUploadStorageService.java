package uk.co.nstauthority.hydrocarbonmaturation.file;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ClientService;

@Service
public class FileUploadStorageService {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadStorageService.class);

  private final S3ClientService s3ClientService;

  @Autowired
  FileUploadStorageService(S3ClientService s3ClientService) {
    this.s3ClientService = s3ClientService;
  }

  public void uploadFile(InputStream inputStream, UploadedFile uploadedFile) {
    var objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(uploadedFile.getFileSizeBytes());
    objectMetadata.setContentType(uploadedFile.getFileContentType());

    s3ClientService.storeFile(uploadedFile.getBucketName(), uploadedFile.getS3Key(), inputStream, objectMetadata);
    LOGGER.debug("Completed upload of file {}", uploadedFile.getFilename());
  }

  void deleteFile(UploadedFile uploadedFile) {
    s3ClientService.deleteFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());
  }

  InputStream downloadFile(UploadedFile uploadedFile) {
    return s3ClientService.fetchFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());
  }
}
