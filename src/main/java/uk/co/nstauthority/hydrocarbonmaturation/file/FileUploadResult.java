package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class represents a document upload response sent as a JSON object to Javascript when a file is uploaded
 */
public class FileUploadResult {

  private final String fileId;
  private final String fileName;
  private final Long size;
  private final String contentType;
  private final UploadErrorType errorType;

  public FileUploadResult(String fileId, String fileName, long size, String contentType, UploadErrorType errorType) {
    this.fileId = fileId;
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.errorType = errorType;
  }

  public static FileUploadResult virusFound(String sanitisedFileName,
                                            MultipartFile multipartFile) {
    return new FileUploadResult(null, sanitisedFileName, multipartFile.getSize(), multipartFile.getContentType(), UploadErrorType.VIRUS_FOUND_IN_FILE);
  }

  public static FileUploadResult extensionNotAllowed(String sanitisedFileName,
                                                    MultipartFile multipartFile) {
    return new FileUploadResult(null, sanitisedFileName, multipartFile.getSize(), multipartFile.getContentType(), UploadErrorType.EXTENSION_NOT_ALLOWED);
  }

  public static FileUploadResult maxFileSizeExceeded(String sanitisedFileName,
                                                    MultipartFile multipartFile) {
    return new FileUploadResult(null, sanitisedFileName, multipartFile.getSize(), multipartFile.getContentType(), UploadErrorType.MAX_FILE_SIZE_EXCEEDED);
  }

  public static FileUploadResult valid(UUID id, String sanitisedFileName, MultipartFile multipartFile) {
    return new FileUploadResult(id.toString(), sanitisedFileName, multipartFile.getSize(), multipartFile.getContentType(), null);
  }

  public Optional<String> getFileId() {
    return Optional.ofNullable(fileId);
  }

  public String getFileName() {
    return fileName;
  }

  public Long getSize() {
    return size;
  }

  public String getContentType() {
    return contentType;
  }

  public Optional<UploadErrorType> getErrorType() {
    return Optional.ofNullable(errorType);
  }

  public Optional<String> getErrorMessage() {
    return getErrorType().map(UploadErrorType::getErrorMessage);
  }

  public boolean isValid() {
    return this.errorType == null && this.fileId != null;
  }
}

