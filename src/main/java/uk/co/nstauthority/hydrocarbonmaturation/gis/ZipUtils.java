package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
  private static final double THRESHOLD_ENTRIES = 10000;
  private static final double THRESHOLD_RATIO = 10;

  private ZipUtils() {
  }

  static List<String> getFilesInZip(ZipInputStream zis) throws IOException {
    var totalEntryArchive = 0;
    List<String> files = new ArrayList<>();

    var nextEntry = zis.getNextEntry();
    while (nextEntry != null) {
      totalEntryArchive++;
      if (totalEntryArchive > THRESHOLD_ENTRIES) {
        throw new SecurityException(
            "Too many entries in zip file, can lead to inodes exhaustion of the system");
      }

      var compressionRatio = getCompressionRatio(nextEntry);
      if (compressionRatio > THRESHOLD_RATIO) {
        throw new SecurityException(
            "Ratio between compressed and uncompressed data is highly suspicious in zip file, could be a Zip Bomb Attack");
      }

      if (nextEntry.isDirectory()) {
        continue;
      }

      files.add(nextEntry.getName().toLowerCase());

      nextEntry = zis.getNextEntry();
    }
    zis.closeEntry();

    return files;
  }

  static InputStream getInputStreamForFileInZip(ZipInputStream zis, String filename) throws IOException {
    var totalEntryArchive = 0;

    var nextEntry = zis.getNextEntry();
    while (nextEntry != null) {
      totalEntryArchive++;
      if (totalEntryArchive > THRESHOLD_ENTRIES) {
        throw new SecurityException(
            "Too many entries in this zip file %s, can lead to inodes exhaustion of the system"
                .formatted(filename)
        );
      }

      var compressionRatio = getCompressionRatio(nextEntry);
      if (compressionRatio > THRESHOLD_RATIO) {
        throw new SecurityException(
            "Ratio between compressed and uncompressed data is highly suspicious in zip file %s, could be a Zip Bomb Attack"
                .formatted(filename)
        );
      }

      if (nextEntry.isDirectory()) {
        continue;
      }

      if (nextEntry.getName().equalsIgnoreCase(filename)) {
        return zis;
      }

      nextEntry = zis.getNextEntry();
    }
    zis.closeEntry();

    throw new EOFException("Cannot find %s in zip file".formatted(filename));
  }

  private static long getCompressionRatio(ZipEntry nextEntry) {
    if (nextEntry.getSize() == 0) {
      return 0;
    }

    return nextEntry.getSize() / nextEntry.getCompressedSize();
  }
}