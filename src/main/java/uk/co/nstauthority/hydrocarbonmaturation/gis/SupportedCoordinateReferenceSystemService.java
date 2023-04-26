package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.SpatialReference;
import java.io.IOException;
import mil.nga.crs.wkt.CRSReader;
import org.springframework.stereotype.Service;

@Service
class SupportedCoordinateReferenceSystemService {

  SpatialReference getSpatialReferenceFromWkt(String wellKnownText) {
    return SpatialReference.create(wellKnownText);
  }

  SupportedCoordinateReferenceSystem getSupportedCoordinateReferenceSystemFromWkt(String wellKnownText) {
    try {
      var crs = CRSReader.readCoordinateReferenceSystem(wellKnownText);

      return switch (crs.getType()) {
        case GEOGRAPHIC -> getSupportedCoordinateReferenceSystemFromGeoWkt(wellKnownText);
        case PROJECTED -> getSupportedCoordinateReferenceSystemFromProjectedWkt(wellKnownText);
        default -> throw new IllegalArgumentException("Unsupported CRS type %s in WKT".formatted(crs.getType()));
      };
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not read WKT", e);
    }
  }

  private static SupportedCoordinateReferenceSystem getSupportedCoordinateReferenceSystemFromGeoWkt(
      String wellKnownText) throws IOException {
    var geoCoordinateReferenceSystem = CRSReader.readGeo(wellKnownText);
    var name = geoCoordinateReferenceSystem.getName();
    return SupportedCoordinateReferenceSystem.fromGeographicCsName(name);
  }

  private static SupportedCoordinateReferenceSystem getSupportedCoordinateReferenceSystemFromProjectedWkt(
      String wellKnownText) throws IOException {
    var projectedCoordinateReferenceSystem = CRSReader.readProjectedCompat(wellKnownText);
    var projectedName = projectedCoordinateReferenceSystem.getName();
    var geographicName = projectedCoordinateReferenceSystem.getBaseName();

    return SupportedCoordinateReferenceSystem.fromProjectionCsNameAndGeographicCsName(projectedName, geographicName);
  }
}
