package uk.co.nstauthority.hydrocarbonmaturation.gis.esri;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorImportFromESRIShape;
import com.esri.core.geometry.ShapeImportFlags;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a shapefile as a list of geometries.
 * Based on ShapefileGeometryCursor
 * Updated by Fivium Ltd 2023
 * Source: github.com/Esri/samples-geometry-api-java
 * License: Apache License, Version 2.0
 */
public class ShapefileGeometryLoader {
  private static final Logger LOGGER = LoggerFactory.getLogger(ShapefileGeometryLoader.class);
  public static final int EXPECTED_FILE_CODE = 9994;
  public static final int EXPECTED_VERSION = 1000;

  private final OperatorImportFromESRIShape importFromEsriShape = OperatorImportFromESRIShape.local();
  private final MixedEndianDataInputStream inputStream;
  private final List<Geometry> geometryList;
  private final ShapefileHeader shapefileHeader;

  private int position = 0;

  /**
   * Parses input stream as a shapefile. Performs basic validation of shapefile header.
   *
   * <table>
   *   <caption><b>Shapefile Header</b></caption>
   *   <tr>
   *     <th>Field</th>
   *     <th>Type</th>
   *     <th>Endianness</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>File Code</td>
   *     <td>int</td>
   *     <td>Big</td>
   *     <td>The file code that identifies the file as a shapefile. Should be set to 9994.</td>
   *   </tr>
   *   <tr>
   *     <td>Unused Field</td>
   *     <td>byte[20]</td>
   *     <td>N/A</td>
   *     <td>Twenty bytes of unused data.</td>
   *   </tr>
   *   <tr>
   *     <td>File Length</td>
   *     <td>int</td>
   *     <td>Big</td>
   *     <td>The length of the shapefile in 16-bit words (i.e., the total number of 16-bit words in the file).</td>
   *   </tr>
   *   <tr>
   *     <td>Version</td>
   *     <td>int</td>
   *     <td>Little</td>
   *     <td>The version of the shapefile format. Should be set to 1000.</td>
   *   </tr>
   *   <tr>
   *     <td>Shape Type</td>
   *     <td>int</td>
   *     <td>Little</td>
   *     <td>The type of the shape objects in the file (e.g., point, polyline, polygon, etc.). <br/>
   *     See the ShapeType enum for possible values.</td>
   *   </tr>
   *   <tr>
   *     <td>MBR</td>
   *     <td>double[4]</td>
   *     <td>Little</td>
   *     <td>Minimum bounding rectangle (MBR) of all shapes contained within the dataset; <br/>
   *     four doubles in the following order: min X, min Y, max X, max Y
   *   </tr>
   *   <tr>
   *     <td>Range of Z</td>
   *     <td>double[2]</td>
   *     <td>Little</td>
   *     <td>Two doubles in the following order: min Z, max Z
   *   </tr>
   *   <tr>
   *     <td>Range of M</td>
   *     <td>double[2]</td>
   *     <td>Little</td>
   *     <td>Two doubles in the following order: min M, max M
   *   </tr>
   * </table>
   *
   * @param stream Shapefile input stream to parse
   * @throws IOException if stream contains unsupported file code or version
   */
  public ShapefileGeometryLoader(InputStream stream) throws IOException {

    this.inputStream = new MixedEndianDataInputStream(stream);

    shapefileHeader = parseShapefileHeader();

    geometryList = parseRecordsAndBuildGeometryList();
  }

  public List<Geometry> getGeometryList() {
    return geometryList;
  }

  public ShapefileHeader getShapefileHeader() {
    return shapefileHeader;
  }

  private ShapefileHeader parseShapefileHeader() throws IOException {
    /*
      Byte 0 File Code 9994 Integer Big
      */
    var fileCode = inputStream.readInt();
    if (fileCode != EXPECTED_FILE_CODE) {
      throw new IOException("File code " + fileCode + " is not supported.");
    }

    /*
      Byte 4 Unused 0 Integer Big
      Byte 8 Unused 0 Integer Big
      Byte 12 Unused 0 Integer Big
      Byte 16 Unused 0 Integer Big
      Byte 20 Unused 0 Integer Big
      */
    inputStream.skipBytes(20);

    /*
      Byte 24 File length - Integer Big
      */
    final var fileLength = inputStream.read16BitWordAsInt();

    /*
      Byte 28 Version 1000 Integer Little
      */
    var version = this.inputStream.readLittleEndianInt();
    if (version != EXPECTED_VERSION) {
      throw new IOException("Version " + version + " is not supported.");
    }

    /*
      Byte 32 Shape Type - Integer Little
      */
    var shpTypeId = this.inputStream.readLittleEndianInt();
    var geometryType = geometryTypeFromShpType(shpTypeId);
    LOGGER.debug("Geometry type: {}", geometryType.name());

    /*
      Byte 36 Bounding Box Xmin Double Little
      Byte 44 Bounding Box Ymin Double Little
      Byte 52 Bounding Box Xmax Double Little
      Byte 60 Bounding Box Ymax Double Little
      Byte 68* Bounding Box Zmin Double Little
      Byte 76* Bounding Box Zmax Double Little
      Byte 84* Bounding Box Mmin Double Little
      Byte 92* Bounding Box Mmax Double Little
      */
    this.inputStream.readLittleEndianDouble(); // xmin
    this.inputStream.readLittleEndianDouble(); // ymin
    this.inputStream.readLittleEndianDouble(); // xmax
    this.inputStream.readLittleEndianDouble(); // ymax
    this.inputStream.readLittleEndianDouble(); // zmin
    this.inputStream.readLittleEndianDouble(); // zmax
    this.inputStream.readLittleEndianDouble(); // mmin
    this.inputStream.readLittleEndianDouble(); // mmax

    // Shapefile header is always 100 bytes long
    position = 100;

    return new ShapefileHeader(fileLength, geometryType);
  }

  private List<Geometry> parseRecordsAndBuildGeometryList() throws IOException {
    var geometries = new ArrayList<Geometry>();

    while (position < shapefileHeader.fileLength) {
      var recordHeader = parseRecordHeader();
      var recordByteBuffer = parseRecord(recordHeader);

      var geometry = importFromEsriShape.execute(
          ShapeImportFlags.ShapeImportDefaults,
          shapefileHeader.geometryType,
          recordByteBuffer
      );

      geometries.add(geometry);
    }

    return geometries;
  }

  private RecordHeader parseRecordHeader() throws IOException {
    var recordId = inputStream.readInt();
    LOGGER.debug("Record ID: {}", recordId);

    var recordSizeBytes = inputStream.read16BitWordAsInt();
    LOGGER.debug("Record {} size {}", recordId, recordSizeBytes);

    // Record header is always 8 bytes long
    position += 8;

    return new RecordHeader(recordId, recordSizeBytes);
  }

  private ByteBuffer parseRecord(RecordHeader recordHeader) throws IOException {
    var bytes = new byte[recordHeader.recordSizeBytes];
    var read = inputStream.read(bytes);
    if (read == -1) {
      throw new IOException("No more data because the end of the file has been reached.");
    }

    position += recordHeader.recordSizeBytes;

    return ByteBuffer.wrap(bytes);
  }

  /**
   * From ESRI shapefile spec:
   * 0 Null Shape
   * 1 Point
   * 3 PolyLine
   * 5 Polygon
   * 8 MultiPoint
   * 11 PointZ
   * 13 PolyLineZ
   * 15 PolygonZ
   * 18 MultiPointZ
   * 21 PointM
   * 23 PolyLineM
   * 25 PolygonM
   * 28 MultiPointM
   * 31 MultiPatch
   * therefore final digit suffices to determine type (PolyLine, PolyLineM, PolylineZ are 1, 13 and 23 respectively).
   *
   * @param shapeTypeId shape type id from shapefile
   * @return the geometry type
   */
  private Geometry.Type geometryTypeFromShpType(int shapeTypeId) {
    var shapeType = shapeTypeId % 10;

    return switch (shapeType) {
      case 1 -> Geometry.Type.Point;
      case 3 -> Geometry.Type.Polyline;
      case 5 -> Geometry.Type.Polygon;
      case 8 -> Geometry.Type.MultiPoint;
      default -> Geometry.Type.Unknown;
    };
  }

  public record ShapefileHeader(int fileLength, Geometry.Type geometryType) {}

  record RecordHeader(int recordId, int recordSizeBytes) {}
}