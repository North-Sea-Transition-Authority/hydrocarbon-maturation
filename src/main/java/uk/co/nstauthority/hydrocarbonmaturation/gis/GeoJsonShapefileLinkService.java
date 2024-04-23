package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.FileUploadRequest;
import uk.co.fivium.fileuploadlibrary.core.FileUsage;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.file.MultipartFileInputStream;

@Service
public class GeoJsonShapefileLinkService {
  public static final String GEOJSON_CONTENT_TYPE = "application/json";
  private final FileService fileService;
  private final GeoJsonShapefileLinkRepository geoJsonShapefileLinkRepository;
  private final ShapefileConversionService shapefileConversionService;

  @Autowired
  GeoJsonShapefileLinkService(FileService fileService,
                              GeoJsonShapefileLinkRepository geoJsonShapefileLinkRepository,
                              ShapefileConversionService shapefileConversionService) {
    this.fileService = fileService;
    this.geoJsonShapefileLinkRepository = geoJsonShapefileLinkRepository;
    this.shapefileConversionService = shapefileConversionService;
  }

  GeoJsonShapefileLink storeShapefileAsGeoJson(UploadedFile uploadedFile,
                                               ShapefileZipContents shapefileZip) throws IOException {
    var geoJson = shapefileConversionService.getGeoJsonFromShapefileZip(shapefileZip);
    return storeGeoJson(uploadedFile, geoJson, null);
  }

  public GeoJsonShapefileLink storeGeoJson(UploadedFile shapefile,
                                           String geoJson,
                                           @Nullable User user) throws IOException {
    var geoJsonShapefileLink = geoJsonShapefileLinkRepository.findByShapefile(shapefile);

    if (geoJsonShapefileLink.isPresent()) {
      return geoJsonShapefileLink.get();
    }

    var multipartFileInputStream = new MultipartFileInputStream(
        shapefile.getName(),
        GEOJSON_CONTENT_TYPE,
        geoJson.length(),
        IOUtils.toInputStream(geoJson, Charset.defaultCharset())
    );

    var upload = fileService.upload(getBuilderFileUploadRequestFunction(multipartFileInputStream, user));

    var uploadedFile = fileService.find(upload.getFileId())
        .orElseThrow(() -> new RuntimeException("Uploaded file not found"));

    return createGeoJsonShapefileLinkAndUpdateUsage(shapefile, uploadedFile);
  }

  @VisibleForTesting
  Function<FileUploadRequest.Builder, FileUploadRequest> getBuilderFileUploadRequestFunction(
      MultipartFileInputStream multipartFileInputStream, @Nullable User user) {
    return builder -> {

      if (user != null) {
        builder.withUploadedBy(user.wuaId().toString());
      }

      return builder
          .withMultipartFile(multipartFileInputStream)
          .build();
    };
  }

  private GeoJsonShapefileLink createGeoJsonShapefileLinkAndUpdateUsage(UploadedFile shapefile,
                                                                        UploadedFile geoJsonUploadedFile) {
    var geoJsonShapefileLink = new GeoJsonShapefileLink();
    geoJsonShapefileLink.setShapefile(shapefile);
    geoJsonShapefileLink.setGeoJsonFile(geoJsonUploadedFile);

    geoJsonShapefileLinkRepository.save(geoJsonShapefileLink);

    fileService.updateUsage(
        geoJsonUploadedFile,
        getBuilderFileUsageFunction(shapefile)
    );

    return geoJsonShapefileLink;
  }

  @VisibleForTesting
  Function<FileUsage.Builder, FileUsage> getBuilderFileUsageFunction(UploadedFile shapefile) {
    return builder -> builder
        .withUsageId(shapefile.getUsageId())
        .withUsageType(shapefile.getUsageType())
        .withDocumentType(FileDocumentType.SHAPEFILE_GEOJSON.name())
        .build();
  }

  @Transactional
  public void deleteGeojson(UploadedFile shapefile) {
    var geoJsonFile = geoJsonShapefileLinkRepository.findByShapefile(shapefile)
        .map(GeoJsonShapefileLink::getGeoJsonFile);

    geoJsonShapefileLinkRepository.deleteByShapefile(shapefile);

    geoJsonFile.ifPresent(fileService::delete);
  }
}
