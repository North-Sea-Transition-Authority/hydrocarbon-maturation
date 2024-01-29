package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Entity
@Table(name = "permeability_details")
class PermeabilityDetail implements ReservoirSubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "reservoir_submission_detail_id")
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  private Boolean hasPermeability;

  private BigDecimal permeabilityMin;

  private BigDecimal permeabilityMax;

  private BigDecimal permeabilityAverage;

  @Enumerated(EnumType.STRING)
  private AverageMethod averageMethodUsed;

  @Column(name = "source")
  private PermeabilitySource permeabilitySource;

  private String sourceOther;

  private String permeabilityComments;

  private BigDecimal kvKhAverage;

  public ReservoirSubmissionDetail getReservoirSubmissionDetail() {
    return reservoirSubmissionDetail;
  }

  @Override
  public void setReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    this.reservoirSubmissionDetail = reservoirSubmissionDetail;
  }

  public Boolean getHasPermeability() {
    return hasPermeability;
  }

  public void setHasPermeability(Boolean hasPermeability) {
    this.hasPermeability = hasPermeability;
  }

  public BigDecimal getPermeabilityMin() {
    return permeabilityMin;
  }

  public void setPermeabilityMin(BigDecimal permeabilityMin) {
    this.permeabilityMin = permeabilityMin;
  }

  public BigDecimal getPermeabilityMax() {
    return permeabilityMax;
  }

  public void setPermeabilityMax(BigDecimal permeabilityMax) {
    this.permeabilityMax = permeabilityMax;
  }

  public BigDecimal getPermeabilityAverage() {
    return permeabilityAverage;
  }

  public void setPermeabilityAverage(BigDecimal permeabilityAverage) {
    this.permeabilityAverage = permeabilityAverage;
  }

  public AverageMethod getAverageMethodUsed() {
    return averageMethodUsed;
  }

  public void setAverageMethodUsed(
      AverageMethod averageMethodUsed) {
    this.averageMethodUsed = averageMethodUsed;
  }

  public PermeabilitySource getPermeabilitySource() {
    return permeabilitySource;
  }

  public void setPermeabilitySource(
      PermeabilitySource permeabilitySource) {
    this.permeabilitySource = permeabilitySource;
  }

  public String getPermeabilityComments() {
    return permeabilityComments;
  }

  public void setPermeabilityComments(String permeabilityComments) {
    this.permeabilityComments = permeabilityComments;
  }

  public BigDecimal getKvKhAverage() {
    return kvKhAverage;
  }

  public void setKvKhAverage(BigDecimal kvKhAverage) {
    this.kvKhAverage = kvKhAverage;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getSourceOther() {
    return sourceOther;
  }

  public void setSourceOther(String sourceOther) {
    this.sourceOther = sourceOther;
  }
}
