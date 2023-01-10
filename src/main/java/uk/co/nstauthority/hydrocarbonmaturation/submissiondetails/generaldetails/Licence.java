package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.GeneralDetailChildEntity;

@Entity
@Table(name = "general_details_licences")
public class Licence implements GeneralDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @JoinColumn(name = "general_details_id")
  @ManyToOne
  private GeneralDetail generalDetail;

  private String licenceReference;

  private Integer licenceId;

  @Override
  public void setId(UUID id) {
    this.id = id;
  }

  @Override
  public void setGeneralDetail(GeneralDetail generalDetail) {
    this.generalDetail = generalDetail;
  }

  public UUID getId() {
    return id;
  }

  public GeneralDetail getGeneralDetail() {
    return generalDetail;
  }

  public String getLicenceReference() {
    return licenceReference;
  }

  public void setLicenceReference(String licenceReference) {
    this.licenceReference = licenceReference;
  }

  public Integer getLicenceId() {
    return licenceId;
  }

  public void setLicenceId(Integer id) {
    this.licenceId = id;
  }
}
