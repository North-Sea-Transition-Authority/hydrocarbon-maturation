package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadException extends RuntimeException {
  public FileUploadException(MultipartFile multipartFile, IOException e) {
    super(String.format("Could not upload file %s", multipartFile.getOriginalFilename()), e);
  }
}
