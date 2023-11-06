package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Entity
@Table(name = "probability_distributions")
public class ProbabilityDistribution implements SubmissionDetailChildEntity, ReservoirSubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  @ManyToOne
  @JoinColumn(name = "reservoir_submission_detail_id")
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @Enumerated(EnumType.STRING)
  private ProbabilityDistributionType distributionType;

  private String otherDistributionType;

  @Enumerated(EnumType.STRING)
  private ProbabilityDistributionFieldInstance instance;

  private BigDecimal minValue;

  private BigDecimal p99;

  private BigDecimal p90;

  @Column(name = "one_c")
  private BigDecimal oneC;

  private BigDecimal modeValue;

  private BigDecimal p50;

  @Column(name = "two_c")
  private BigDecimal twoC;

  private BigDecimal meanValue;

  private BigDecimal p10;

  @Column(name = "three_c")
  private BigDecimal threeC;

  private BigDecimal p1;

  private BigDecimal maxValue;

  private BigDecimal constantValue;

  private BigDecimal mostLikelyValue;

  @Column(name = "one_p")
  private BigDecimal oneP;

  @Column(name = "two_p")
  private BigDecimal twoP;

  @Column(name = "three_p")
  private BigDecimal threeP;

  private String comments;

  public ProbabilityDistribution() {
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

  public ReservoirSubmissionDetail getReservoirSubmissionDetail() {
    return reservoirSubmissionDetail;
  }

  public void setReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    this.reservoirSubmissionDetail = reservoirSubmissionDetail;
  }

  public ProbabilityDistributionType getDistributionType() {
    return distributionType;
  }

  public void setDistributionType(
      ProbabilityDistributionType distributionType) {
    this.distributionType = distributionType;
  }

  public String getOtherDistributionType() {
    return otherDistributionType;
  }

  public void setOtherDistributionType(String otherDistributionType) {
    this.otherDistributionType = otherDistributionType;
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

  public BigDecimal getOneC() {
    return oneC;
  }

  public void setOneC(BigDecimal oneC) {
    this.oneC = oneC;
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

  public BigDecimal getTwoC() {
    return twoC;
  }

  public void setTwoC(BigDecimal twoC) {
    this.twoC = twoC;
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

  public BigDecimal getThreeC() {
    return threeC;
  }

  public void setThreeC(BigDecimal threeC) {
    this.threeC = threeC;
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

  public BigDecimal getOneP() {
    return oneP;
  }

  public void setOneP(BigDecimal oneP) {
    this.oneP = oneP;
  }

  public BigDecimal getTwoP() {
    return twoP;
  }

  public void setTwoP(BigDecimal twoP) {
    this.twoP = twoP;
  }

  public BigDecimal getThreeP() {
    return threeP;
  }

  public void setThreeP(BigDecimal threeP) {
    this.threeP = threeP;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }
}