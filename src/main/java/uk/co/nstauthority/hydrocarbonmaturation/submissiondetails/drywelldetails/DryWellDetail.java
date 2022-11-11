package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

@Entity
@Table(name = "dry_well_details")
class DryWellDetail {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private String dryWellId;

  private String dryWellName;

  private BigDecimal latitudeDegrees;

  private BigDecimal latitudeMinutes;

  private BigDecimal latitudeSeconds;

  private BigDecimal longitudeDegrees;

  private BigDecimal longitudeMinutes;

  private BigDecimal longitudeSeconds;

  @Enumerated(EnumType.STRING)
  private Hemisphere longitudeHemisphere;

  @Enumerated(EnumType.STRING)
  private ReservoirDatum datum;

  private String totalDepthFormation;

  private String totalDepthFormationComments;

  private String dataAcquisitionProgramme;

  private String geophysicalUpdateCommentary;

  @Enumerated(EnumType.STRING)
  private DryHoleType dryHoleType;

  private String dryHoleDescription;

  private String dryHoleScope;

  private String dryHoleComments;

  private BigDecimal dryHoleCost;

  public String getTotalDepthFormationComments() {
    return totalDepthFormationComments;
  }

  public void setTotalDepthFormationComments(String totalDepthFormationComments) {
    this.totalDepthFormationComments = totalDepthFormationComments;
  }

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

  public String getDryWellId() {
    return dryWellId;
  }

  public void setDryWellId(String dryWellId) {
    this.dryWellId = dryWellId;
  }

  public String getDryWellName() {
    return dryWellName;
  }

  public void setDryWellName(String dryWellText) {
    this.dryWellName = dryWellText;
  }

  public BigDecimal getLatitudeDegrees() {
    return latitudeDegrees;
  }

  public void setLatitudeDegrees(BigDecimal latitudeDegrees) {
    this.latitudeDegrees = latitudeDegrees;
  }

  public BigDecimal getLatitudeMinutes() {
    return latitudeMinutes;
  }

  public void setLatitudeMinutes(BigDecimal latitudeMinutes) {
    this.latitudeMinutes = latitudeMinutes;
  }

  public BigDecimal getLatitudeSeconds() {
    return latitudeSeconds;
  }

  public void setLatitudeSeconds(BigDecimal latitudeSeconds) {
    this.latitudeSeconds = latitudeSeconds;
  }

  public BigDecimal getLongitudeDegrees() {
    return longitudeDegrees;
  }

  public void setLongitudeDegrees(BigDecimal longitudeDegrees) {
    this.longitudeDegrees = longitudeDegrees;
  }

  public BigDecimal getLongitudeMinutes() {
    return longitudeMinutes;
  }

  public void setLongitudeMinutes(BigDecimal longitudeMinutes) {
    this.longitudeMinutes = longitudeMinutes;
  }

  public BigDecimal getLongitudeSeconds() {
    return longitudeSeconds;
  }

  public void setLongitudeSeconds(BigDecimal longitudeSeconds) {
    this.longitudeSeconds = longitudeSeconds;
  }

  public Hemisphere getLongitudeHemisphere() {
    return longitudeHemisphere;
  }

  public void setLongitudeHemisphere(
      Hemisphere longitudeHemisphere) {
    this.longitudeHemisphere = longitudeHemisphere;
  }

  public ReservoirDatum getDatum() {
    return datum;
  }

  public void setDatum(ReservoirDatum datum) {
    this.datum = datum;
  }

  public String getTotalDepthFormation() {
    return totalDepthFormation;
  }

  public void setTotalDepthFormation(String totalDepthFormation) {
    this.totalDepthFormation = totalDepthFormation;
  }

  public String getDataAcquisitionProgramme() {
    return dataAcquisitionProgramme;
  }

  public void setDataAcquisitionProgramme(String dataAcquisitionProgramme) {
    this.dataAcquisitionProgramme = dataAcquisitionProgramme;
  }

  public String getGeophysicalUpdateCommentary() {
    return geophysicalUpdateCommentary;
  }

  public void setGeophysicalUpdateCommentary(String geophysicalUpdateCommentary) {
    this.geophysicalUpdateCommentary = geophysicalUpdateCommentary;
  }

  public DryHoleType getDryHoleType() {
    return dryHoleType;
  }

  public void setDryHoleType(
      DryHoleType dryHoleType) {
    this.dryHoleType = dryHoleType;
  }

  public String getDryHoleDescription() {
    return dryHoleDescription;
  }

  public void setDryHoleDescription(String dryHoleDescription) {
    this.dryHoleDescription = dryHoleDescription;
  }

  public String getDryHoleScope() {
    return dryHoleScope;
  }

  public void setDryHoleScope(String dryHoleScope) {
    this.dryHoleScope = dryHoleScope;
  }

  public String getDryHoleComments() {
    return dryHoleComments;
  }

  public void setDryHoleComments(String dryHoleComments) {
    this.dryHoleComments = dryHoleComments;
  }

  public BigDecimal getDryHoleCost() {
    return dryHoleCost;
  }

  public void setDryHoleCost(BigDecimal dryHoleCost) {
    this.dryHoleCost = dryHoleCost;
  }
}
