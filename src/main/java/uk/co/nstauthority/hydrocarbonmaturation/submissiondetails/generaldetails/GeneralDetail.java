package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.math.BigDecimal;
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

  private BigDecimal percentageLocatedInUkcs;

  @Enumerated(EnumType.STRING)
  private InformationSource informationSource;

  private String otherInformationSource;

  private String licenceApplicationInfoSource;

  private String licenceApplicationReference;

  private Integer licenceApplicationId;

  private Integer organisationId;

  private String organisationName;

  private String organisationRegisteredNumber;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public String getProspectName() {
    return prospectName;
  }

  public void setProspectName(String prospectName) {
    this.prospectName = prospectName;
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public void setOnshoreOffshore(
      OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
  }

  public Boolean getProspectLocatedInUkcs() {
    return prospectLocatedInUkcs;
  }

  public void setProspectLocatedInUkcs(Boolean prospectLocatedInUkcs) {
    this.prospectLocatedInUkcs = prospectLocatedInUkcs;
  }

  public BigDecimal getPercentageLocatedInUkcs() {
    return percentageLocatedInUkcs;
  }

  public void setPercentageLocatedInUkcs(BigDecimal percentageLocatedInUkcs) {
    this.percentageLocatedInUkcs = percentageLocatedInUkcs;
  }

  public InformationSource getInformationSource() {
    return informationSource;
  }

  public void setInformationSource(
      InformationSource informationSource) {
    this.informationSource = informationSource;
  }

  public String getOtherInformationSource() {
    return otherInformationSource;
  }

  public void setOtherInformationSource(String otherInformationSource) {
    this.otherInformationSource = otherInformationSource;
  }

  public String getLicenceApplicationInfoSource() {
    return licenceApplicationInfoSource;
  }

  public void setLicenceApplicationInfoSource(String licenceApplicationInfoSource) {
    this.licenceApplicationInfoSource = licenceApplicationInfoSource;
  }

  public String getLicenceApplicationReference() {
    return licenceApplicationReference;
  }

  public void setLicenceApplicationReference(String licenceApplicationReference) {
    this.licenceApplicationReference = licenceApplicationReference;
  }

  public Integer getLicenceApplicationId() {
    return licenceApplicationId;
  }

  public void setLicenceApplicationId(Integer licenceApplicationId) {
    this.licenceApplicationId = licenceApplicationId;
  }

  public Integer getOrganisationId() {
    return organisationId;
  }

  public void setOrganisationId(Integer organisationId) {
    this.organisationId = organisationId;
  }

  public String getOrganisationName() {
    return organisationName;
  }

  public void setOrganisationName(String organisationUName) {
    this.organisationName = organisationUName;
  }

  public String getOrganisationRegisteredNumber() {
    return organisationRegisteredNumber;
  }

  public void setOrganisationRegisteredNumber(String organisationRegisteredNumber) {
    this.organisationRegisteredNumber = organisationRegisteredNumber;
  }
}
