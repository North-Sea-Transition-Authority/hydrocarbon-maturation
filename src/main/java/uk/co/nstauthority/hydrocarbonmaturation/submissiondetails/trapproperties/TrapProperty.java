package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "trap_properties")
public class TrapProperty implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private String trapDescription;

  @Enumerated(EnumType.STRING)
  private TrapType trapType;

  private String otherTrapType;

  private BigDecimal latitudeDegrees;

  private BigDecimal latitudeMinutes;

  private BigDecimal latitudeSeconds;

  private BigDecimal longitudeDegrees;

  private BigDecimal longitudeMinutes;

  private BigDecimal longitudeSeconds;

  @Enumerated(EnumType.STRING)
  private ReservoirDatum datum;

  private String centreOfInterestQuadrantBlock;

  @Enumerated(EnumType.STRING)
  private Hemisphere longitudeHemisphere;

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

  public String getTrapDescription() {
    return trapDescription;
  }

  public void setTrapDescription(String trapDescription) {
    this.trapDescription = trapDescription;
  }

  public TrapType getTrapType() {
    return trapType;
  }

  public void setTrapType(TrapType trapType) {
    this.trapType = trapType;
  }

  public String getOtherTrapType() {
    return otherTrapType;
  }

  public void setOtherTrapType(String otherTrapType) {
    this.otherTrapType = otherTrapType;
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

  public String getCentreOfInterestQuadrantBlock() {
    return centreOfInterestQuadrantBlock;
  }

  public void setCentreOfInterestQuadrantBlock(String centreOfInterestQuadrantBlock) {
    this.centreOfInterestQuadrantBlock = centreOfInterestQuadrantBlock;
  }
}
