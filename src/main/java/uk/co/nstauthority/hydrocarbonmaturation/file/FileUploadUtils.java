package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class FileUploadUtils {

  private static final long BYTE = 1L;
  private static final long KIB = BYTE << 10;
  private static final long MIB = KIB << 10;
  private static final long GIB = MIB << 10;


  private static final DecimalFormat DEC_FORMAT = new DecimalFormat("#.##");

  private FileUploadUtils() {
  }

  public static String fileSizeFormatter(Long size) {
    if (size < 0) {
      throw new IllegalArgumentException("Invalid file size: " + size);
    }
    if (size >= GIB) {
      return formatSize(size, GIB, "GiB");
    }
    if (size >= MIB) {
      return formatSize(size, MIB, "MiB");
    }
    if (size >= KIB) {
      return formatSize(size, KIB, "KiB");
    }
    return formatSize(size, BYTE, "Bytes");
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

  private static String formatSize(long size, long divider, String unitName) {
    return DEC_FORMAT.format((double) size / divider) + " " + unitName;
  }
}
