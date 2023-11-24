package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "general_details")
public class GeneralDetail implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Boolean resourceLocatedInUkcs;
  private Boolean resourceLocatedInEngland;

  private BigDecimal percentageLocatedInUkcs;
  private BigDecimal percentageLocatedInEngland;

  @Enumerated(EnumType.STRING)
  private InformationSource informationSource;

  private String otherInformationSource;

  private String licenceApplicationInfoSource;

  private String licenceApplicationReference;

  private Integer licenceApplicationId;

  private LocalDate relinquishmentDate;

  private LocalDate fieldStoppedProducingDate;

  private String capitalProjectScopeDelivered;

  @Enumerated(EnumType.STRING)
  private DriveMechanism currentDriveMechanism;

  private String otherDriveMechanism;

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

  public Boolean getResourceLocatedInUkcs() {
    return resourceLocatedInUkcs;
  }

  public void setResourceLocatedInUkcs(Boolean resourceLocatedInUkcs) {
    this.resourceLocatedInUkcs = resourceLocatedInUkcs;
  }

  public Boolean getResourceLocatedInEngland() {
    return resourceLocatedInEngland;
  }

  public void setResourceLocatedInEngland(Boolean resourceLocatedInEngland) {
    this.resourceLocatedInEngland = resourceLocatedInEngland;
  }

  public BigDecimal getPercentageLocatedInUkcs() {
    return percentageLocatedInUkcs;
  }

  public void setPercentageLocatedInUkcs(BigDecimal percentageLocatedInUkcs) {
    this.percentageLocatedInUkcs = percentageLocatedInUkcs;
  }

  public BigDecimal getPercentageLocatedInEngland() {
    return percentageLocatedInEngland;
  }

  public void setPercentageLocatedInEngland(BigDecimal percentageLocatedInEngland) {
    this.percentageLocatedInEngland = percentageLocatedInEngland;
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

  public LocalDate getRelinquishmentDate() {
    return relinquishmentDate;
  }

  public void setRelinquishmentDate(LocalDate relinquishmentDate) {
    this.relinquishmentDate = relinquishmentDate;
  }

  public LocalDate getFieldStoppedProducingDate() {
    return fieldStoppedProducingDate;
  }

  public void setFieldStoppedProducingDate(LocalDate fieldStoppedProducingDate) {
    this.fieldStoppedProducingDate = fieldStoppedProducingDate;
  }

  public String getCapitalProjectScopeDelivered() {
    return capitalProjectScopeDelivered;
  }

  public void setCapitalProjectScopeDelivered(String capitalProjectScopeDelivered) {
    this.capitalProjectScopeDelivered = capitalProjectScopeDelivered;
  }

  public DriveMechanism getCurrentDriveMechanism() {
    return currentDriveMechanism;
  }

  public void setCurrentDriveMechanism(DriveMechanism currentDriveMechanism) {
    this.currentDriveMechanism = currentDriveMechanism;
  }

  public String getOtherDriveMechanism() {
    return otherDriveMechanism;
  }

  public void setOtherDriveMechanism(String otherDriveMechanism) {
    this.otherDriveMechanism = otherDriveMechanism;
  }
}
