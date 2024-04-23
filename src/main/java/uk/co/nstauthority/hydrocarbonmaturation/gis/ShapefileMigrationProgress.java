package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.NonSimpleResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "shapefile_migration_progress")
class ShapefileMigrationProgress {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;
  private String polygonId;
  private String filename;
  @Enumerated(EnumType.STRING)
  private ShapefileMigrationStatus status;
  @Enumerated(EnumType.STRING)
  private NonSimpleResult.Reason invalidGeometryReason;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getPolygonId() {
    return polygonId;
  }

  public void setPolygonId(String polygonId) {
    this.polygonId = polygonId;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public ShapefileMigrationStatus getStatus() {
    return status;
  }

  public void setStatus(ShapefileMigrationStatus status) {
    this.status = status;
  }

  public NonSimpleResult.Reason getInvalidGeometryReason() {
    return invalidGeometryReason;
  }

  public void setInvalidGeometryReason(NonSimpleResult.Reason invalidGeometryReason) {
    this.invalidGeometryReason = invalidGeometryReason;
  }
}
