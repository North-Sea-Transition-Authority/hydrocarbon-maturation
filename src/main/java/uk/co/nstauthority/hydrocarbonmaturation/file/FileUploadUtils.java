package uk.co.nstauthority.hydrocarbonmaturation.file;

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
}
