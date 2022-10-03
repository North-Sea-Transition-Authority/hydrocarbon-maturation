package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.InputStream;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ClientService;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3DeleteFileFailedException;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ObjectNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

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

  public UploadedFile uploadFileAndSaveRecord(VirtualFolder virtualFolder, MultipartFile multipartFile, long fileSize,
                                       String filename, String contentType) {
    var objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(fileSize);
    objectMetadata.setContentType(contentType);

    var s3Key = createS3KeyWithPath(virtualFolder);

    var uploadedFile = createUploadedFile(
        s3ClientService.getDefaultBucketName(),
        virtualFolder,
        s3Key,
        filename,
        contentType,
        fileSize);
    // Persist upload file record first, so if it fails, we don't upload. If upload fails, we will roll back
    uploadedFileRepository.save(uploadedFile);

    try {
      s3ClientService.storeFile(s3ClientService.getDefaultBucketName(), s3Key, multipartFile.getInputStream(), objectMetadata);
    } catch (Exception e) {
      throw new FileUploadException(multipartFile, e);
    }

    return uploadedFile;
  }

  public UploadedFile findUploadedFileOrThrow(UUID uploadedFileId) {
    return uploadedFileRepository.findById(uploadedFileId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            String.format("Could not find uploaded file with ID %s", uploadedFileId)));
  }

  public DeleteOutcome deleteFile(UploadedFile uploadedFile) {
    try {
      s3ClientService.deleteFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());
    } catch (S3DeleteFileFailedException | S3ObjectNotFoundException e) {
      return DeleteOutcome.INTERNAL_SERVER_ERROR;
    }

    uploadedFileRepository.deleteById(uploadedFile.getId());

    return DeleteOutcome.SUCCESS;
  }

  InputStream downloadFile(UploadedFile uploadedFile) {
    return s3ClientService.fetchFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());
  }

  public FileUploadTemplate buildFileUploadTemplate(String uploadUrl, String deleteUrl) {
    return new FileUploadTemplate(
        ReverseRouter.route(on(FileUploadsDownloadController.class).download(null)),
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

  public void updateFileDescription(UploadedFile uploadedFile, String uploadedFileDescription) {
    uploadedFile.setDescription(uploadedFileDescription);
    uploadedFileRepository.save(uploadedFile);
  }
}
