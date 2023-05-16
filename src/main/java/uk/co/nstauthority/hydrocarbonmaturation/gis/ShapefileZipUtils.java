package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ShapefileZipUtils {
  private static final double THRESHOLD_ENTRIES = 10000;
  private static final double THRESHOLD_RATIO = 10;

  private ShapefileZipUtils() {
  }

  static ShapefileZipContents getShapefileZipContents(ZipInputStream zis,
                                                      String filename,
                                                      BiConsumer<ShapefileZipContents.ShapefileZipBuilder, ZipEntry> fileHandler)
      throws IOException {
    var shapefileZipBuilder = ShapefileZipContents.builder();
    List<String> files = new ArrayList<>();
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

      files.add(nextEntry.getName().toLowerCase());

      fileHandler.accept(shapefileZipBuilder, nextEntry);

      nextEntry = zis.getNextEntry();
    }
    zis.closeEntry();

    return shapefileZipBuilder
        .withFilenames(files)
        .build();
  }

  private static long getCompressionRatio(ZipEntry nextEntry) {
    if (nextEntry.getSize() == 0) {
      return 0;
    }

    return nextEntry.getSize() / nextEntry.getCompressedSize();
  }

}