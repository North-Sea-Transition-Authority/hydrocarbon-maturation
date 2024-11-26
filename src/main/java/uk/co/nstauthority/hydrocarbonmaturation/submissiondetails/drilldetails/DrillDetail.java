package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
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

  private BigDecimal topReservoirLatitudeDegrees;

  private BigDecimal topReservoirLatitudeMinutes;

  private BigDecimal topReservoirLatitudeSeconds;

  private BigDecimal topReservoirLongitudeDegrees;

  private BigDecimal topReservoirLongitudeMinutes;

  private BigDecimal topReservoirLongitudeSeconds;

  @Enumerated(EnumType.STRING)
  private Hemisphere topReservoirLongitudeHemisphere;

  @Enumerated(EnumType.STRING)
  private ReservoirDatum topReservoirDatum;

  @Enumerated(EnumType.STRING)
  private Formation totalDepthFormation;

  private String totalDepthFormationManual;

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

  public BigDecimal getTopReservoirLatitudeDegrees() {
    return topReservoirLatitudeDegrees;
  }

  public void setTopReservoirLatitudeDegrees(BigDecimal latitudeDegrees) {
    this.topReservoirLatitudeDegrees = latitudeDegrees;
  }

  public BigDecimal getTopReservoirLatitudeMinutes() {
    return topReservoirLatitudeMinutes;
  }

  public void setTopReservoirLatitudeMinutes(BigDecimal latitudeMinutes) {
    this.topReservoirLatitudeMinutes = latitudeMinutes;
  }

  public BigDecimal getTopReservoirLatitudeSeconds() {
    return topReservoirLatitudeSeconds;
  }

  public void setTopReservoirLatitudeSeconds(BigDecimal latitudeSeconds) {
    this.topReservoirLatitudeSeconds = latitudeSeconds;
  }

  public BigDecimal getTopReservoirLongitudeDegrees() {
    return topReservoirLongitudeDegrees;
  }

  public void setTopReservoirLongitudeDegrees(BigDecimal longitudeDegrees) {
    this.topReservoirLongitudeDegrees = longitudeDegrees;
  }

  public BigDecimal getTopReservoirLongitudeMinutes() {
    return topReservoirLongitudeMinutes;
  }

  public void setTopReservoirLongitudeMinutes(BigDecimal longitudeMinutes) {
    this.topReservoirLongitudeMinutes = longitudeMinutes;
  }

  public BigDecimal getTopReservoirLongitudeSeconds() {
    return topReservoirLongitudeSeconds;
  }

  public void setTopReservoirLongitudeSeconds(BigDecimal longitudeSeconds) {
    this.topReservoirLongitudeSeconds = longitudeSeconds;
  }

  public Hemisphere getTopReservoirLongitudeHemisphere() {
    return topReservoirLongitudeHemisphere;
  }

  public void setTopReservoirLongitudeHemisphere(
      Hemisphere longitudeHemisphere) {
    this.topReservoirLongitudeHemisphere = longitudeHemisphere;
  }

  public ReservoirDatum getTopReservoirDatum() {
    return topReservoirDatum;
  }

  public void setTopReservoirDatum(ReservoirDatum datum) {
    this.topReservoirDatum = datum;
  }

  public Formation getTotalDepthFormation() {
    return totalDepthFormation;
  }

  public void setTotalDepthFormation(Formation totalDepthFormation) {
    this.totalDepthFormation = totalDepthFormation;
  }

  public String getTotalDepthFormationManual() {
    return totalDepthFormationManual;
  }

  public void setTotalDepthFormationManual(String totalDepthFormationManual) {
    this.totalDepthFormationManual = totalDepthFormationManual;
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
