package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.VirtualFolder;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;

@Service
class GeoJsonShapefileLinkService {
  public static final String GEOJSON_CONTENT_TYPE = "application/json";
  private final FileUploadService fileUploadService;
  private final GeoJsonShapefileLinkRepository geoJsonShapefileLinkRepository;

  @Autowired
  GeoJsonShapefileLinkService(FileUploadService fileUploadService,
                              GeoJsonShapefileLinkRepository geoJsonShapefileLinkRepository) {
    this.fileUploadService = fileUploadService;
    this.geoJsonShapefileLinkRepository = geoJsonShapefileLinkRepository;
  }

  public GeoJsonShapefileLink storeGeoJson(SupportingDocument shapefile, String geoJson, String shapefileName) {
    var geoJsonUploadedFile = createdGeoJsonUploadedFile(geoJson, shapefileName);

    var geoJsonShapefileLink = createGeoJsonShapefileLink(shapefile, geoJsonUploadedFile);

    var geoJsonInputStream = IOUtils.toInputStream(geoJson, Charset.defaultCharset());
    fileUploadService.uploadFile(geoJsonInputStream, geoJsonUploadedFile);

    return geoJsonShapefileLink;
  }

  private UploadedFile createdGeoJsonUploadedFile(String geoJson, String shapefileName) {
    return fileUploadService.createUploadedFile(
        VirtualFolder.SHAPEFILE_GEOJSON,
        geoJson.length(),
        shapefileName,
        GEOJSON_CONTENT_TYPE
    );
  }

  private GeoJsonShapefileLink createGeoJsonShapefileLink(SupportingDocument shapefile, UploadedFile geoJsonUploadedFile) {
    var geoJsonShapefileLink = new GeoJsonShapefileLink();
    geoJsonShapefileLink.setShapefile(shapefile);
    geoJsonShapefileLink.setGeoJsonFile(geoJsonUploadedFile);

    geoJsonShapefileLinkRepository.save(geoJsonShapefileLink);
    
    return geoJsonShapefileLink;
  }
}
