package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails;

import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

@MappedSuperclass
public class DrillDetail {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Integer wellId;

  private String wellReference;

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

  public Integer getWellId() {
    return wellId;
  }

  public void setWellId(Integer wellId) {
    this.wellId = wellId;
  }

  public String getWellReference() {
    return wellReference;
  }

  public void setWellReference(String wellReference) {
    this.wellReference = wellReference;
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

  public String getTotalDepthFormationComments() {
    return totalDepthFormationComments;
  }

  public void setTotalDepthFormationComments(String totalDepthFormationComments) {
    this.totalDepthFormationComments = totalDepthFormationComments;
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
}
