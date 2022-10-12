package uk.co.nstauthority.hydrocarbonmaturation.file;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.InputStream;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ClientService;

@Service
public class FileUploadService {

  private final S3ClientService s3ClientService;
  private final UploadedFileRepository uploadedFileRepository;
  private final FileUploadConfig fileUploadConfig;
  private final Clock clock;

  @Autowired
  public FileUploadService(S3ClientService s3ClientService,
                           UploadedFileRepository uploadedFileRepository,
                           FileUploadConfig fileUploadConfig,
                           Clock clock) {
    this.s3ClientService = s3ClientService;
    this.uploadedFileRepository = uploadedFileRepository;
    this.fileUploadConfig = fileUploadConfig;
    this.clock = clock;
  }

  public UploadedFile createdAndSaveUploadedFile(VirtualFolder virtualFolder, long fileSize, String filename, String contentType) {
    var s3Key = createS3KeyWithPath(virtualFolder);

    var uploadedFile = createUploadedFile(
        s3ClientService.getDefaultBucketName(),
        virtualFolder,
        s3Key,
        filename,
        contentType,
        fileSize);

    uploadedFileRepository.save(uploadedFile);

    return uploadedFile;
  }

  public void uploadFile(MultipartFile multipartFile, UploadedFile uploadedFile) {
    var objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(uploadedFile.getFileSizeBytes());
    objectMetadata.setContentType(uploadedFile.getFileContentType());

    try {
      s3ClientService.storeFile(s3ClientService.getDefaultBucketName(), uploadedFile.getS3Key(), multipartFile.getInputStream(),
          objectMetadata);
    } catch (Exception e) {
      throw new FileUploadException(multipartFile, e);
    }
  }

  public UploadedFile findUploadedFileOrThrow(UUID uploadedFileId) {
    return uploadedFileRepository.findById(uploadedFileId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            String.format("Could not find uploaded file with ID %s", uploadedFileId)));
  }

  public void deleteFile(UploadedFile uploadedFile) {
    s3ClientService.deleteFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());

    uploadedFileRepository.deleteById(uploadedFile.getId());
  }

  public InputStream downloadFile(UploadedFile uploadedFile) {
    return s3ClientService.fetchFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());
  }

  public FileUploadTemplate buildFileUploadTemplate(String downloadUrl, String uploadUrl, String deleteUrl) {
    return new FileUploadTemplate(
        downloadUrl,
        uploadUrl,
        deleteUrl,
        fileUploadConfig.maxAllowedSize().toString(),
        fileUploadConfig.allowedExtensions()
    );
  }

  private String createS3KeyWithPath(VirtualFolder virtualFolder) {
    return virtualFolder + "/" + UUID.randomUUID();
  }

  private UploadedFile createUploadedFile(String bucketName,
                                          VirtualFolder virtualFolder,
                                          String s3Key,
                                          String filename,
                                          String contentType,
                                          long fileSize) {

    var uploadedFile = new UploadedFile();
    uploadedFile.setS3Key(s3Key);
    uploadedFile.setBucketName(bucketName);
    uploadedFile.setVirtualFolder(virtualFolder);
    uploadedFile.setFilename(filename);
    uploadedFile.setFileContentType(contentType);
    uploadedFile.setFileSizeBytes(fileSize);
    uploadedFile.setUploadedTimeStamp(clock.instant());

    return uploadedFile;
  }

  public String sanitiseFilename(String filename) {
    return filename.replaceAll(fileUploadConfig.filenameDisallowedCharactersRegex(), "_");
  }

  public List<UploadedFile> getFileUploadsByIdList(List<UUID> uploadedFileIdList) {
    return uploadedFileRepository.findAllByIdIn(uploadedFileIdList);
  }

  @Transactional
  public void updateFileDescription(UploadedFile uploadedFile, String uploadedFileDescription) {
    uploadedFile.setDescription(uploadedFileDescription);
    uploadedFileRepository.save(uploadedFile);
  }
}
