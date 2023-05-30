package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "shapefile_migration_progress")
class ShapefileMigrationProgress {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  private Integer migrationRecordIndex;

  private String polygonId;

  @Enumerated(EnumType.STRING)
  private ShapefileMigrationStatus status;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Integer getMigrationRecordIndex() {
    return migrationRecordIndex;
  }

  public void setMigrationRecordIndex(Integer migrationRecordIndex) {
    this.migrationRecordIndex = migrationRecordIndex;
  }

  public String getPolygonId() {
    return polygonId;
  }

  public void setPolygonId(String polygonId) {
    this.polygonId = polygonId;
  }

  public ShapefileMigrationStatus getStatus() {
    return status;
  }

  public void setStatus(ShapefileMigrationStatus status) {
    this.status = status;
  }
}
