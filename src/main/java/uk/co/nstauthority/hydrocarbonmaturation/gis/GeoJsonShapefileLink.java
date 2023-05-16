package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;

@Entity
@Table(name = "geojson_shapefile_link")
class GeoJsonShapefileLink {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @JoinColumn(name = "shapefile_sd_id")
  @ManyToOne
  private SupportingDocument shapefile;

  @JoinColumn(name = "geojson_uf_id")
  @ManyToOne
  private UploadedFile geoJsonFile;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public SupportingDocument getShapefile() {
    return shapefile;
  }

  public void setShapefile(SupportingDocument shapefile) {
    this.shapefile = shapefile;
  }

  public UploadedFile getGeoJsonFile() {
    return geoJsonFile;
  }

  public void setGeoJsonFile(UploadedFile geoJsonFile) {
    this.geoJsonFile = geoJsonFile;
  }
}
