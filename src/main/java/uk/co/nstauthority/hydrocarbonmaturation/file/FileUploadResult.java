package uk.co.nstauthority.hydrocarbonmaturation.file;

import javax.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record FileUploadResult(@Nullable String fileId, String fileName, Long size, String contentType,
                               @Nullable UploadErrorType errorType) {
  private static FileUploadResult fromMultipartFile(String fileId, String fileName, MultipartFile multipartFile,
                                                    UploadErrorType errorType) {
    return new FileUploadResult(fileId, fileName, multipartFile.getSize(), multipartFile.getContentType(), errorType);
  }

  public static FileUploadResult error(String fileName, MultipartFile multipartFile, UploadErrorType errorType) {
    return FileUploadResult.fromMultipartFile(null, fileName, multipartFile, errorType);
  }

  public static FileUploadResult valid(String fileId, String fileName, MultipartFile multipartFile) {
    return FileUploadResult.fromMultipartFile(fileId, fileName, multipartFile, null);
  }

  public String getErrorMessage() {
    if (errorType == null) {
      return "";
    }

    return errorType.getErrorMessage();
  }

  public boolean isValid() {
    return this.errorType == null && this.fileId != null;
  }
}

