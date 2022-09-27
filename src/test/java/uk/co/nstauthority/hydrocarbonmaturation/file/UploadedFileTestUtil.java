package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.UUID;

public class UploadedFileTestUtil {

  private UploadedFileTestUtil() {
    throw new AssertionError();
  }

  static UploadedFile createValidUploadedFile() {
    var fileSize = 123L;
    var filename = "file.txt";
    var contentType = "text";
    return createValidUploadedFile(filename, fileSize, contentType);
  }

  public static UploadedFile createValidUploadedFile(String filename, long fileSize, String contentType) {
    var uuid = UUID.randomUUID();
    var uploadedFile = new UploadedFile();
    var s3Key = "testKey";
    uploadedFile.setId(uuid);
    uploadedFile.setS3Key(s3Key);
    uploadedFile.setFilename(filename);
    uploadedFile.setFileSizeBytes(fileSize);
    uploadedFile.setFileContentType(contentType);

    return uploadedFile;
  }
}
