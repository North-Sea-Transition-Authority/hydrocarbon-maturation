package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;

@Service
public class ShapefileConversionService {

  private final ShapefileZipService shapefileZipService;
  private final GeoJsonExportService geoJsonExportService;
  private final FileService fileService;

  @Autowired
  ShapefileConversionService(ShapefileZipService shapefileZipService,
                             GeoJsonExportService geoJsonExportService,
                             FileService fileService) {
    this.shapefileZipService = shapefileZipService;
    this.geoJsonExportService = geoJsonExportService;
    this.fileService = fileService;
  }

  public String convertShapefileToGeoJson(UploadedFile shapefileZipDocument)  {
    var shapefileZipInputStream = getInputStream(shapefileZipDocument);

    var byteArrayInputStream = getByteArrayInputStream(shapefileZipInputStream);
    var shapefileZip = shapefileZipService.getShapefileZipContents(byteArrayInputStream, shapefileZipDocument.getName());

    return getGeoJsonFromShapefileZip(shapefileZip);
  }

  public String getGeoJsonFromShapefileZip(ShapefileZipContents shapefileZip) {
    return geoJsonExportService.exportGeometryAsGeoJson(
        shapefileZip.thresholds(),
        shapefileZip.coordinateReferenceSystem(),
        shapefileZip.geometries()
    );
  }

  private InputStream getInputStream(UploadedFile shapefileZipDocument) {
    try {
      var shapefileZipDocumentResource = Optional.ofNullable(fileService.download(shapefileZipDocument).getBody())
          .orElseThrow(() -> new IllegalArgumentException("Shapefile zip could not be found"));

      return shapefileZipDocumentResource.getInputStream();
    } catch (IOException e) {
      throw new IllegalArgumentException("Shapefile zip could not be read", e);
    }
  }

  private static ByteArrayInputStream getByteArrayInputStream(InputStream shapefileZipInputStream) {
    try {
      return new ByteArrayInputStream(shapefileZipInputStream.readAllBytes());
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not read shapefile", e);
    }
  }
}
