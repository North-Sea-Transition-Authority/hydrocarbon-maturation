package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;

@Service
public class ShapefileConversionService {

  private final ShapefileZipService shapefileZipService;
  private final GeoJsonExportService geoJsonExportService;
  private final FileUploadService fileUploadService;

  @Autowired
  ShapefileConversionService(ShapefileZipService shapefileZipService,
                             GeoJsonExportService geoJsonExportService,
                             FileUploadService fileUploadService) {
    this.shapefileZipService = shapefileZipService;
    this.geoJsonExportService = geoJsonExportService;
    this.fileUploadService = fileUploadService;
  }

  public String convertShapefileToGeoJson(SupportingDocument shapefileZipDocument)  {
    var uploadedZip = shapefileZipDocument.getUploadedFile();
    var shapefileZipInputStream = fileUploadService.downloadFile(uploadedZip);

    var byteArrayInputStream = getByteArrayInputStream(shapefileZipInputStream);
    var shapefileZip = shapefileZipService.getShapefileZipContents(byteArrayInputStream, uploadedZip.getFilename());

    return getGeoJsonFromShapefileZip(shapefileZip);
  }

  private static ByteArrayInputStream getByteArrayInputStream(InputStream shapefileZipInputStream) {
    try {
      return new ByteArrayInputStream(shapefileZipInputStream.readAllBytes());
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not read shapefile", e);
    }
  }

  private String getGeoJsonFromShapefileZip(ShapefileZipContents shapefileZip) {
    return geoJsonExportService.exportGeometryAsGeoJson(
        shapefileZip.thresholds(),
        shapefileZip.coordinateReferenceSystem(),
        shapefileZip.geometries()
    );
  }
}
