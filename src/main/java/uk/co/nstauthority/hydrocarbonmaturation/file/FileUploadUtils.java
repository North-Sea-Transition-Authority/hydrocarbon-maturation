package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.List;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class FileUploadUtils {

  private FileUploadUtils() {
  }

  /**
   * Converts the displayed file size into a more readable string with the appropriate unit. Eg. 29573 bytes -> 29MB
   * @param fileSize the size of the file in bytes
   * @return a String specifying the size of the file with the corresponding byte unit
   */
  public static String fileSizeFormatter(Long fileSize) {
    int i = -1;
    String[] byteUnits = {" kB", " MB", " GB", " TB"};
    do {
      fileSize = fileSize / 1024;
      i++;
    } while (fileSize > 1024);

    return Math.max(fileSize, 1) + byteUnits[i];
  }

  public static ResponseEntity<InputStreamResource> getFileResourceResponseEntity(UploadedFile uploadedFile,
                                                                                  InputStreamResource inputStreamResource) {
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(uploadedFile.getFileSizeBytes())
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"%s\"".formatted(uploadedFile.getFilename())
        )
        .body(inputStreamResource);
  }

  public static List<UUID> getUploadIdList(List<FileUploadDetail> fileUploadDetails) {
    return fileUploadDetails.stream()
        .map(FileUploadDetail::getUploadedFileId)
        .map(UUID::fromString)
        .toList();
  }
}
