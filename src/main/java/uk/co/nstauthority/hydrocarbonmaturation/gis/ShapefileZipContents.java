package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.util.List;

public record ShapefileZipContents(
    String shapefileName,
    List<String> filenames,
    SpatialReference thresholds,
    SupportedCoordinateReferenceSystem coordinateReferenceSystem,
    List<Geometry> geometries,
    ShapefileAttributes attributes) {

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String shapefileName;
    private List<String> filenames;
    private SpatialReference thresholds;
    private SupportedCoordinateReferenceSystem coordinateReferenceSystem;
    private List<Geometry> geometries;
    private ShapefileAttributes attributes;

    private Builder() {
    }

    public Builder withShapefileName(String shapefileName) {
      this.shapefileName = shapefileName;
      return this;
    }

    public Builder withThresholds(SpatialReference thresholds) {
      this.thresholds = thresholds;
      return this;
    }

    public Builder withCoordinateReferenceSystem(SupportedCoordinateReferenceSystem coordinateReferenceSystem) {
      this.coordinateReferenceSystem = coordinateReferenceSystem;
      return this;
    }

    public Builder withGeometries(List<Geometry> geometries) {
      this.geometries = geometries;
      return this;
    }

    public Builder withFilenames(List<String> filenames) {
      this.filenames = filenames;
      return this;
    }

    public Builder withAttributes(ShapefileAttributes attributes) {
      this.attributes = attributes;
      return this;
    }

    public ShapefileZipContents build() {
      return new ShapefileZipContents(shapefileName, filenames, thresholds, coordinateReferenceSystem, geometries, attributes);
    }
  }
}
