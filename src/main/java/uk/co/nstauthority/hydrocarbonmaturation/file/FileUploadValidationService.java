package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.virus.VirusCheckService;

@Service
public class FileUploadValidationService {
  private final VirusCheckService virusCheckService;
  private final FileUploadConfig fileUploadConfig;

  @Autowired
  public FileUploadValidationService(VirusCheckService virusCheckService, FileUploadConfig fileUploadConfig) {
    this.virusCheckService = virusCheckService;
    this.fileUploadConfig = fileUploadConfig;
  }

  public UploadErrorType validateFileUpload(MultipartFile multipartFile, long fileSize, String filename) {
    if (!isFileExtensionAllowed(filename)) {
      return UploadErrorType.EXTENSION_NOT_ALLOWED;
    }

    if (!isFileSizeAllowed(fileSize)) {
      return UploadErrorType.MAX_FILE_SIZE_EXCEEDED;
    }

    try {
      if (virusCheckService.hasVirus(multipartFile.getInputStream())) {
        return UploadErrorType.VIRUS_FOUND_IN_FILE;
      }
    } catch (Exception e) {
      throw new FileUploadException(multipartFile, e);
    }

    return null;
  }

  private boolean isFileExtensionAllowed(String filename) {
    String lowercase = filename.toLowerCase();
    return Arrays.stream(fileUploadConfig.allowedExtensions().split(","))
        .map(String::trim)
        .anyMatch(lowercase::endsWith);
  }

  private boolean isFileSizeAllowed(long fileSize) {
    return fileSize <= fileUploadConfig.maxAllowedSize();
  }
}
