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

@Entity
@Table(name = "trap_properties")
class TrapProperty {

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
  private Hemisphere longitudeHemisphere;

  public UUID getId() {
    return id;
  }

  public TrapProperty setId(UUID id) {
    this.id = id;
    return this;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public TrapProperty setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
    return this;
  }

  public String getTrapDescription() {
    return trapDescription;
  }

  public TrapProperty setTrapDescription(String trapDescription) {
    this.trapDescription = trapDescription;
    return this;
  }

  public TrapType getTrapType() {
    return trapType;
  }

  public TrapProperty setTrapType(TrapType trapType) {
    this.trapType = trapType;
    return this;
  }

  public String getOtherTrapType() {
    return otherTrapType;
  }

  public TrapProperty setOtherTrapType(String otherTrapType) {
    this.otherTrapType = otherTrapType;
    return this;
  }

  public BigDecimal getLatitudeDegrees() {
    return latitudeDegrees;
  }

  public TrapProperty setLatitudeDegrees(BigDecimal latitudeDegrees) {
    this.latitudeDegrees = latitudeDegrees;
    return this;
  }

  public BigDecimal getLatitudeMinutes() {
    return latitudeMinutes;
  }

  public TrapProperty setLatitudeMinutes(BigDecimal latitudeMinutes) {
    this.latitudeMinutes = latitudeMinutes;
    return this;
  }

  public BigDecimal getLatitudeSeconds() {
    return latitudeSeconds;
  }

  public TrapProperty setLatitudeSeconds(BigDecimal latitudeSeconds) {
    this.latitudeSeconds = latitudeSeconds;
    return this;
  }

  public BigDecimal getLongitudeDegrees() {
    return longitudeDegrees;
  }

  public TrapProperty setLongitudeDegrees(BigDecimal longitudeDegrees) {
    this.longitudeDegrees = longitudeDegrees;
    return this;
  }

  public BigDecimal getLongitudeMinutes() {
    return longitudeMinutes;
  }

  public TrapProperty setLongitudeMinutes(BigDecimal longitudeMinutes) {
    this.longitudeMinutes = longitudeMinutes;
    return this;
  }

  public BigDecimal getLongitudeSeconds() {
    return longitudeSeconds;
  }

  public TrapProperty setLongitudeSeconds(BigDecimal longitudeSeconds) {
    this.longitudeSeconds = longitudeSeconds;
    return this;
  }

  public Hemisphere getLongitudeHemisphere() {
    return longitudeHemisphere;
  }

  public TrapProperty setLongitudeHemisphere(Hemisphere hemisphere) {
    this.longitudeHemisphere = hemisphere;
    return this;
  }
}
