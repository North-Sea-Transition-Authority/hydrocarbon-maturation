package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "probability_distributions")
public class ProbabilityDistribution implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  @Enumerated(EnumType.STRING)
  private ProbabilityDistributionType distributionType;

  @Enumerated(EnumType.STRING)
  private ProbabilityDistributionFieldInstance instance;

  private BigDecimal minValue;

  private BigDecimal p99;

  private BigDecimal p90;

  private BigDecimal modeValue;

  private BigDecimal p50;

  private BigDecimal meanValue;

  private BigDecimal p10;

  private BigDecimal p1;

  private BigDecimal maxValue;

  private BigDecimal constantValue;

  private BigDecimal mostLikelyValue;

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

  public ProbabilityDistributionType getDistributionType() {
    return distributionType;
  }

  public void setDistributionType(
      ProbabilityDistributionType distributionType) {
    this.distributionType = distributionType;
  }

  public ProbabilityDistributionFieldInstance getInstance() {
    return instance;
  }

  public void setInstance(
      ProbabilityDistributionFieldInstance instance) {
    this.instance = instance;
  }

  public BigDecimal getMinValue() {
    return minValue;
  }

  public void setMinValue(BigDecimal minValue) {
    this.minValue = minValue;
  }

  public BigDecimal getP99() {
    return p99;
  }

  public void setP99(BigDecimal p99) {
    this.p99 = p99;
  }

  public BigDecimal getP90() {
    return p90;
  }

  public void setP90(BigDecimal p90) {
    this.p90 = p90;
  }

  public BigDecimal getModeValue() {
    return modeValue;
  }

  public void setModeValue(BigDecimal modeValue) {
    this.modeValue = modeValue;
  }

  public BigDecimal getP50() {
    return p50;
  }

  public void setP50(BigDecimal p50) {
    this.p50 = p50;
  }

  public BigDecimal getMeanValue() {
    return meanValue;
  }

  public void setMeanValue(BigDecimal meanValue) {
    this.meanValue = meanValue;
  }

  public BigDecimal getP10() {
    return p10;
  }

  public void setP10(BigDecimal p10) {
    this.p10 = p10;
  }

  public BigDecimal getP1() {
    return p1;
  }

  public void setP1(BigDecimal p1) {
    this.p1 = p1;
  }

  public BigDecimal getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(BigDecimal maxValue) {
    this.maxValue = maxValue;
  }

  public BigDecimal getConstantValue() {
    return constantValue;
  }

  public void setConstantValue(BigDecimal constantValue) {
    this.constantValue = constantValue;
  }

  public BigDecimal getMostLikelyValue() {
    return mostLikelyValue;
  }

  public void setMostLikelyValue(BigDecimal mostLikelyValue) {
    this.mostLikelyValue = mostLikelyValue;
  }
}