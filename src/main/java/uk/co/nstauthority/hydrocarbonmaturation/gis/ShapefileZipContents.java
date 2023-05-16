package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import java.util.List;

public record ShapefileZipContents(
    String shapefileName,
    List<String> filenames,
    SpatialReference thresholds,
    SupportedCoordinateReferenceSystem coordinateReferenceSystem,
    List<Geometry> geometries) {

  public static ShapefileZipBuilder builder() {
    return new ShapefileZipBuilder();
  }

  public static final class ShapefileZipBuilder {
    private String shapefileName;
    private SpatialReference thresholds;
    private SupportedCoordinateReferenceSystem coordinateReferenceSystem;
    private List<Geometry> geometries;
    private List<String> filenames;

    private ShapefileZipBuilder() {
    }

    public ShapefileZipBuilder withShapefileName(String shapefileName) {
      this.shapefileName = shapefileName;
      return this;
    }

    public ShapefileZipBuilder withThresholds(SpatialReference thresholds) {
      this.thresholds = thresholds;
      return this;
    }

    public ShapefileZipBuilder withCoordinateReferenceSystem(SupportedCoordinateReferenceSystem coordinateReferenceSystem) {
      this.coordinateReferenceSystem = coordinateReferenceSystem;
      return this;
    }

    public ShapefileZipBuilder withGeometries(List<Geometry> geometries) {
      this.geometries = geometries;
      return this;
    }

    public ShapefileZipBuilder withFilenames(List<String> filenames) {
      this.filenames = filenames;
      return this;
    }

    public ShapefileZipContents build() {
      return new ShapefileZipContents(shapefileName, filenames, thresholds, coordinateReferenceSystem, geometries);
    }
  }
}
