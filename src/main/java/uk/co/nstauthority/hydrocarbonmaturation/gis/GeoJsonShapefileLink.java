package uk.co.nstauthority.hydrocarbonmaturation.gis;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;

@Entity
@Table(name = "geojson_shapefile_link")
class GeoJsonShapefileLink {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @JoinColumn(name = "shapefile_sd_id")
  @ManyToOne
  private UploadedFile shapefile;

  @JoinColumn(name = "geojson_uf_id")
  @ManyToOne
  private UploadedFile geoJsonFile;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UploadedFile getShapefile() {
    return shapefile;
  }

  public void setShapefile(UploadedFile shapefile) {
    this.shapefile = shapefile;
  }

  public UploadedFile getGeoJsonFile() {
    return geoJsonFile;
  }

  public void setGeoJsonFile(UploadedFile geoJsonFile) {
    this.geoJsonFile = geoJsonFile;
  }
}
