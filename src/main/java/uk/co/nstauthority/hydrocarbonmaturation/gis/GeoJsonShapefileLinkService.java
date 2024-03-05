package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.file.MultipartFileInputStream;

@Service
class GeoJsonShapefileLinkService {
  public static final String GEOJSON_CONTENT_TYPE = "application/json";
  private final FileService fileService;
  private final GeoJsonShapefileLinkRepository geoJsonShapefileLinkRepository;

  @Autowired
  GeoJsonShapefileLinkService(FileService fileService,
                              GeoJsonShapefileLinkRepository geoJsonShapefileLinkRepository) {
    this.fileService = fileService;
    this.geoJsonShapefileLinkRepository = geoJsonShapefileLinkRepository;
  }

  public GeoJsonShapefileLink storeGeoJson(UploadedFile shapefile,
                                           String geoJson,
                                           String shapefileName,
                                           User user) throws IOException {
    var multipartFileInputStream = new MultipartFileInputStream(
        shapefileName,
        GEOJSON_CONTENT_TYPE,
        geoJson.length(),
        IOUtils.toInputStream(geoJson, Charset.defaultCharset())
    );

    var upload = fileService.upload(builder -> builder
        .withMultipartFile(multipartFileInputStream)
        .withUploadedBy(user.wuaId().toString())
        .build());

    var uploadedFile = fileService.find(upload.getFileId())
        .orElseThrow(() -> new RuntimeException("Uploaded file not found"));

    return createGeoJsonShapefileLink(shapefile, uploadedFile);
  }

  private GeoJsonShapefileLink createGeoJsonShapefileLink(UploadedFile shapefile, UploadedFile geoJsonUploadedFile) {
    var geoJsonShapefileLink = new GeoJsonShapefileLink();
    geoJsonShapefileLink.setShapefile(shapefile);
    geoJsonShapefileLink.setGeoJsonFile(geoJsonUploadedFile);

    geoJsonShapefileLinkRepository.save(geoJsonShapefileLink);
    
    return geoJsonShapefileLink;
  }
}
