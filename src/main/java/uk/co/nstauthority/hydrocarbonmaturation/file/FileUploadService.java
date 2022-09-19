package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.time.Clock;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ClientService;
import uk.co.nstauthority.hydrocarbonmaturation.file.virus.VirusCheckService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Service
public class FileUploadService {

  private final VirusCheckService virusCheckService;
  private final S3ClientService s3ClientService;
  private final UploadedFileRepository uploadedFileRepository;
  private final FileUploadConfig fileUploadConfig;
  private final Clock clock;

  @Autowired
  public FileUploadService(VirusCheckService virusCheckService,
                           S3ClientService s3ClientService,
                           UploadedFileRepository uploadedFileRepository,
                           FileUploadConfig fileUploadConfig, Clock clock) {
    this.virusCheckService = virusCheckService;
    this.s3ClientService = s3ClientService;
    this.uploadedFileRepository = uploadedFileRepository;
    this.fileUploadConfig = fileUploadConfig;
    this.clock = clock;
  }

  public FileUploadResult uploadFile(VirtualFolder virtualFolder, MultipartFile multipartFile) throws IOException {
    var fileSize = multipartFile.getSize();
    var filename = multipartFile.getOriginalFilename();
    var contentType = multipartFile.getContentType();

    if (virusCheckService.hasVirus(multipartFile.getInputStream())) {
      return FileUploadResult.virusFound(filename, multipartFile);
    }

    if (!isFileExtensionAllowed(filename)) {
      return FileUploadResult.extensionNotAllowed(filename, multipartFile);
    }

    if (!isFileSizeAllowed(fileSize)) {
      return FileUploadResult.maxFileSizeExceeded(filename, multipartFile);
    }

    var objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(fileSize);
    objectMetadata.setContentType(contentType);

    try (var fileInputStream = new BufferedInputStream(multipartFile.getInputStream())) {
      var s3Key = createNewS3KeyInFolder(virtualFolder);
      s3ClientService.storeFile(s3Key, fileInputStream, objectMetadata);
      var uploadedFile = createUploadedFile(
          s3ClientService.getDefaultBucketName(),
          virtualFolder,
          s3Key,
          filename,
          contentType,
          fileSize);

      uploadedFileRepository.save(uploadedFile);
      return FileUploadResult.valid(uploadedFile.getId(), filename, multipartFile);

    }
  }

  FileDeleteResult deleteFile(UUID uploadedFileId) {
    var uploadedFile = findUploadedFileOrThrow(uploadedFileId);

    s3ClientService.deleteFile(uploadedFile.getS3Key());
    uploadedFileRepository.deleteById(uploadedFile.getId());

    return FileDeleteResult.success(uploadedFileId.toString());
  }

  FileDownload downloadFile(UUID uploadedFileId) {
    var uploadedFile = findUploadedFileOrThrow(uploadedFileId);
    var inputStream = s3ClientService.fetchFile(uploadedFile.getS3Key());

    return new FileDownload(uploadedFile, inputStream);
  }

  public FileUploadTemplate buildFileUploadTemplate(VirtualFolder virtualFolder) {
    return new FileUploadTemplate(
        ReverseRouter.route(on(FileUploadsController.class).download(null)),
        ReverseRouter.route(on(FileUploadsController.class).upload(virtualFolder, null)),
        ReverseRouter.route(on(FileUploadsController.class).delete(null)),
        fileUploadConfig.getMaxAllowedSize().toString(),
        fileUploadConfig.getAllowedExtensions()
    );
  }

  private UploadedFile findUploadedFileOrThrow(UUID uploadedFileId) {
    return uploadedFileRepository.findById(uploadedFileId)
        .orElseThrow(() -> new IllegalStateException(String.format("Could not find file with ID %s", uploadedFileId)));
  }

  private UploadedFile createUploadedFile(String bucketName, VirtualFolder virtualFolder, String s3Key, String filename, String contentType, long fileSize) {

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

  private boolean isFileExtensionAllowed(String filename) {
    String lowercase = filename.toLowerCase();
    return Arrays.stream(fileUploadConfig.getAllowedExtensions().split(","))
        .map(String::trim)
        .anyMatch(lowercase::endsWith);
  }

  private boolean isFileSizeAllowed(long fileSize) {
    return fileSize <= fileUploadConfig.getMaxAllowedSize();
  }

  private String createNewS3KeyInFolder(VirtualFolder virtualFolder) {
    return virtualFolder + "/" + UUID.randomUUID();
  }
}
