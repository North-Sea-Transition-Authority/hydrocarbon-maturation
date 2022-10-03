package uk.co.nstauthority.hydrocarbonmaturation.file.virus;

import org.springframework.web.multipart.MultipartFile;

public class VirusCheckFailedException extends RuntimeException {
  public VirusCheckFailedException(MultipartFile multipartFile, Exception e) {
    super(String.format("Failed to virus scan file %s", multipartFile.getOriginalFilename()), e);
  }
}
