package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Entity
@Table(name = "general_details")
public class GeneralDetail {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private String prospectName;

  @Enumerated(EnumType.STRING)
  private OnshoreOffshore onshoreOffshore;

  private Boolean prospectLocatedInUkcs;

  private Double percentageLocatedInUkcs;

  @Enumerated(EnumType.STRING)
  private InformationSource informationSource;

  private String otherInformationSource;

  private String licenceApplicationInfoSource;

  public GeneralDetail() {
  }

  public UUID getId() {
    return id;
  }

  public GeneralDetail setId(UUID id) {
    this.id = id;
    return this;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public GeneralDetail setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
    return this;
  }

  public String getProspectName() {
    return prospectName;
  }

  public GeneralDetail setProspectName(String prospectName) {
    this.prospectName = prospectName;
    return this;
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public GeneralDetail setOnshoreOffshore(OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
    return this;
  }

  public Boolean getProspectLocatedInUkcs() {
    return prospectLocatedInUkcs;
  }

  public GeneralDetail setProspectLocatedInUkcs(Boolean prospectLocatedInUkcs) {
    this.prospectLocatedInUkcs = prospectLocatedInUkcs;
    return this;
  }

  public Double getPercentageLocatedInUkcs() {
    return percentageLocatedInUkcs;
  }

  public GeneralDetail setPercentageLocatedInUkcs(Double percentageLocatedInUkcs) {
    this.percentageLocatedInUkcs = percentageLocatedInUkcs;
    return this;
  }

  public InformationSource getInformationSource() {
    return informationSource;
  }

  public GeneralDetail setInformationSource(InformationSource informationSource) {
    this.informationSource = informationSource;
    return this;
  }

  public String getOtherInformationSource() {
    return otherInformationSource;
  }

  public GeneralDetail setOtherInformationSource(String otherInformationSource) {
    this.otherInformationSource = otherInformationSource;
    return this;
  }

  public String getLicenceApplicationInfoSource() {
    return licenceApplicationInfoSource;
  }

  public GeneralDetail setLicenceApplicationInfoSource(String licenceApplicationInfoSource) {
    this.licenceApplicationInfoSource = licenceApplicationInfoSource;
    return this;
  }

  @Override
  public String toString() {
    return "GeneralDetail{" +
        "id=" + id +
        ", submissionDetail=" + submissionDetail +
        ", prospectName='" + prospectName + '\'' +
        ", onshoreOffshore=" + onshoreOffshore +
        ", prospectLocatedInUkcs=" + prospectLocatedInUkcs +
        ", percentageLocatedInUkcs=" + percentageLocatedInUkcs +
        ", informationSource=" + informationSource +
        ", otherInformationSource='" + otherInformationSource + '\'' +
        ", licenceApplicationInfoSource='" + licenceApplicationInfoSource + '\'' +
        '}';
  }
}
