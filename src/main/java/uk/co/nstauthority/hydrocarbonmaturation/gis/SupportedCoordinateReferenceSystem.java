package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.SpatialReference;
import jakarta.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

enum SupportedCoordinateReferenceSystem {
  ED50(4230, "GCS_European_1950", OnshoreOffshore.OFFSHORE),
  WGS84(4326, "GCS_WGS_1984", OnshoreOffshore.OFFSHORE),
  OSGB36_NG(27700, "GCS_OSGB_1936", "British_National_Grid", OnshoreOffshore.ONSHORE),
  ETRS89(4258, "GCS_ETRS_1989", OnshoreOffshore.OFFSHORE),
  ;

  private final int epsgCode;
  private final String geographicCsName;
  @Nullable
  private final String projectionCsName;
  private final OnshoreOffshore onshoreOffshore;

  SupportedCoordinateReferenceSystem(int epsgCode,
                                     String geographicCsName,
                                     @Nullable String projectionCsName,
                                     OnshoreOffshore onshoreOffshore) {
    this.epsgCode = epsgCode;
    this.geographicCsName = geographicCsName;
    this.projectionCsName = projectionCsName;
    this.onshoreOffshore = onshoreOffshore;
  }

  SupportedCoordinateReferenceSystem(int epsgCode, String geographicCsName, OnshoreOffshore onshoreOffshore) {
    this(epsgCode, geographicCsName, null, onshoreOffshore);
  }

  public int getEpsgCode() {
    return epsgCode;
  }

  public String getGeoCsName() {
    return geographicCsName;
  }

  public Optional<String> getProjectionCsName() {
    return Optional.ofNullable(projectionCsName);
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public SpatialReference toSpatialReference() {
    return SpatialReference.create(epsgCode);
  }

  @Nullable
  public static SupportedCoordinateReferenceSystem fromGeographicCsName(String geographicCsName) {
    var esriCoordinateReferenceSystems = Arrays.stream(values())
        .filter(esriCoordinateReferenceSystem ->
            esriCoordinateReferenceSystem.getGeoCsName().equalsIgnoreCase(geographicCsName)
                && esriCoordinateReferenceSystem.getProjectionCsName().isEmpty()
        )
        .toList();

    if (esriCoordinateReferenceSystems.isEmpty()) {
      return null;
    }

    if (esriCoordinateReferenceSystems.size() > 1) {
      throw new IllegalArgumentException("No unique coordinate reference system found for " + geographicCsName);
    }

    return esriCoordinateReferenceSystems.get(0);
  }

  @Nullable
  public static SupportedCoordinateReferenceSystem fromProjectionCsNameAndGeographicCsName(String projectionCsName,
                                                                                           String geographicCsName) {
    var esriCoordinateReferenceSystems = Arrays.stream(values())
        .filter(esriCoordinateReferenceSystem -> esriCoordinateReferenceSystem.getProjectionCsName().isPresent())
        .filter(esriCoordinateReferenceSystem ->
            esriCoordinateReferenceSystem.getGeoCsName().equalsIgnoreCase(geographicCsName)
                && esriCoordinateReferenceSystem.getProjectionCsName().get().equalsIgnoreCase(projectionCsName)
        )
        .toList();

    if (esriCoordinateReferenceSystems.isEmpty()) {
      return null;
    }

    if (esriCoordinateReferenceSystems.size() > 1) {
      throw new IllegalArgumentException("No unique coordinate reference system found for " + geographicCsName);
    }

    return esriCoordinateReferenceSystems.get(0);
  }
}
