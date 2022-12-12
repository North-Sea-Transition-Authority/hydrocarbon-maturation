package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Column;
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
@Table(name = "permeability_details")
class PermeabilityDetail implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

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

  private BigDecimal kvKhMin;

  private BigDecimal kvKhMax;

  private BigDecimal kvKhAverage;

  private AverageMethod kvKhAverageMethodUsed;

  private PermeabilitySource kvKhSource;

  private String kvKhSourceOther;
  private String kvKhComments;

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
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

  public BigDecimal getKvKhMin() {
    return kvKhMin;
  }

  public void setKvKhMin(BigDecimal kvKhMin) {
    this.kvKhMin = kvKhMin;
  }

  public BigDecimal getKvKhMax() {
    return kvKhMax;
  }

  public void setKvKhMax(BigDecimal kvKhMax) {
    this.kvKhMax = kvKhMax;
  }

  public BigDecimal getKvKhAverage() {
    return kvKhAverage;
  }

  public void setKvKhAverage(BigDecimal kvKhAverage) {
    this.kvKhAverage = kvKhAverage;
  }

  public AverageMethod getKvKhAverageMethodUsed() {
    return kvKhAverageMethodUsed;
  }

  public void setKvKhAverageMethodUsed(
      AverageMethod kvKhAverageMethodUsed) {
    this.kvKhAverageMethodUsed = kvKhAverageMethodUsed;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public PermeabilitySource getKvKhSource() {
    return kvKhSource;
  }

  public void setKvKhSource(
      PermeabilitySource kvKhSource) {
    this.kvKhSource = kvKhSource;
  }

  public String getKvKhComments() {
    return kvKhComments;
  }

  public void setKvKhComments(String kvKhComments) {
    this.kvKhComments = kvKhComments;
  }

  public String getSourceOther() {
    return sourceOther;
  }

  public void setSourceOther(String sourceOther) {
    this.sourceOther = sourceOther;
  }

  public String getKvKhSourceOther() {
    return kvKhSourceOther;
  }

  public void setKvKhSourceOther(String kvKhSourceOther) {
    this.kvKhSourceOther = kvKhSourceOther;
  }
}
