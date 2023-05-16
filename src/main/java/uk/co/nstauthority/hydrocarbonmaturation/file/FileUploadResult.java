package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadResult {

  @Nullable
  private final String fileId;
  private final String fileName;
  private final Long size;
  private final String contentType;
  @Nullable
  private final UploadErrorType errorType;
  @Nullable
  private final String errorMessage;

  public FileUploadResult(@Nullable String fileId,
                          String fileName,
                          MultipartFile multipartFile,
                          @Nullable UploadErrorType errorType,
                          @Nullable String errorMessage) {
    this.fileId = fileId;
    this.fileName = fileName;
    this.size = multipartFile.getSize();
    this.contentType = multipartFile.getContentType();
    this.errorType = errorType;
    this.errorMessage = errorMessage;
  }

  public static FileUploadResult customError(String fileName, MultipartFile multipartFile, String errorMessage) {
    return new FileUploadResult(null, fileName, multipartFile, UploadErrorType.OTHER_ERRORS_FOUND, errorMessage);
  }

  public static FileUploadResult error(String fileName, MultipartFile multipartFile, UploadErrorType errorType) {
    return new FileUploadResult(null, fileName, multipartFile, errorType, getErrorMessageFromErrorType(errorType));
  }

  public static FileUploadResult valid(String fileId, String fileName, MultipartFile multipartFile) {
    return new FileUploadResult(fileId, fileName, multipartFile, null, null);
  }

  private static String getErrorMessageFromErrorType(UploadErrorType errorType) {
    return Optional.ofNullable(errorType).map(UploadErrorType::getErrorMessage).orElse(null);
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
    return Optional.ofNullable(errorMessage);
  }

  public boolean isValid() {
    return this.errorType == null && this.fileId != null;
  }
}

