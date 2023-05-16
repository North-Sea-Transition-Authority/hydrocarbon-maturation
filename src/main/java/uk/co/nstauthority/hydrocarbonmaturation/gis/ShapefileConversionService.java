package uk.co.nstauthority.hydrocarbonmaturation.gis;

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

    var shapefileZip = shapefileZipService.getShapefileZipContents(shapefileZipInputStream, uploadedZip.getFilename());

    return getGeoJsonFromShapefileZip(shapefileZip);
  }

  private String getGeoJsonFromShapefileZip(ShapefileZipContents shapefileZip) {
    return geoJsonExportService.exportGeometryAsGeoJson(
        shapefileZip.thresholds(),
        shapefileZip.coordinateReferenceSystem(),
        shapefileZip.geometries()
    );
  }
}
