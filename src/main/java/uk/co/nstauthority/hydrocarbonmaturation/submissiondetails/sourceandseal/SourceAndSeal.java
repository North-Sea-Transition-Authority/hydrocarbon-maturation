package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;

@Entity
@Table(name = "source_and_seal_details")
class SourceAndSeal implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  @Enumerated(EnumType.STRING)
  private Formation sourceFormationName;

  private String sourceFormationNameManual;

  @Enumerated(EnumType.STRING)
  private AgeSeries sourceAgeStage;

  private String sourceAgeStageManual;

  @Enumerated(EnumType.STRING)
  private GroupName sourceGroupName;

  @Enumerated(EnumType.STRING)
  private StandardLithology sourceLithology;

  private String sourceLithologyOther;

  @Enumerated(EnumType.STRING)
  private Formation topSealFormationName;

  private String topSealFormationNameManual;

  @Enumerated(EnumType.STRING)
  private AgeSeries topSealAgeStage;

  private String topSealAgeStageManual;

  @Enumerated(EnumType.STRING)
  private GroupName topSealGroupName;

  @Enumerated(EnumType.STRING)
  private StandardLithology topSealLithology;

  private String topSealLithologyOther;

  private BigDecimal sealThickness;

  private BigDecimal topSealFracturePressure;

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

  public Formation getSourceFormationName() {
    return sourceFormationName;
  }

  public void setSourceFormationName(
      Formation sourceFormationName) {
    this.sourceFormationName = sourceFormationName;
  }

  public String getSourceFormationNameManual() {
    return sourceFormationNameManual;
  }

  public void setSourceFormationNameManual(String sourceFormationNameManual) {
    this.sourceFormationNameManual = sourceFormationNameManual;
  }

  public AgeSeries getSourceAgeStage() {
    return sourceAgeStage;
  }

  public void setSourceAgeStage(
      AgeSeries sourceAgeStage) {
    this.sourceAgeStage = sourceAgeStage;
  }

  public String getSourceAgeStageManual() {
    return sourceAgeStageManual;
  }

  public void setSourceAgeStageManual(String sourceAgeStageManual) {
    this.sourceAgeStageManual = sourceAgeStageManual;
  }

  public GroupName getSourceGroupName() {
    return sourceGroupName;
  }

  public void setSourceGroupName(
      GroupName groupName) {
    this.sourceGroupName = groupName;
  }

  public StandardLithology getSourceLithology() {
    return sourceLithology;
  }

  public void setSourceLithology(StandardLithology sourceLithology) {
    this.sourceLithology = sourceLithology;
  }

  public String getSourceLithologyOther() {
    return sourceLithologyOther;
  }

  public void setSourceLithologyOther(String sourceLithologyOther) {
    this.sourceLithologyOther = sourceLithologyOther;
  }

  public Formation getTopSealFormationName() {
    return topSealFormationName;
  }

  public void setTopSealFormationName(
      Formation topSealFormationName) {
    this.topSealFormationName = topSealFormationName;
  }

  public String getTopSealFormationNameManual() {
    return topSealFormationNameManual;
  }

  public void setTopSealFormationNameManual(String topSealFormationNameManual) {
    this.topSealFormationNameManual = topSealFormationNameManual;
  }

  public AgeSeries getTopSealAgeStage() {
    return topSealAgeStage;
  }

  public void setTopSealAgeStage(
      AgeSeries topSealAgeStage) {
    this.topSealAgeStage = topSealAgeStage;
  }

  public String getTopSealAgeStageManual() {
    return topSealAgeStageManual;
  }

  public void setTopSealAgeStageManual(String topSealAgeStageManual) {
    this.topSealAgeStageManual = topSealAgeStageManual;
  }

  public GroupName getTopSealGroupName() {
    return topSealGroupName;
  }

  public void setTopSealGroupName(
      GroupName topSealGroupName) {
    this.topSealGroupName = topSealGroupName;
  }

  public StandardLithology getTopSealLithology() {
    return topSealLithology;
  }

  public void setTopSealLithology(
      StandardLithology topSealLithology) {
    this.topSealLithology = topSealLithology;
  }

  public String getTopSealLithologyOther() {
    return topSealLithologyOther;
  }

  public void setTopSealLithologyOther(String topSealLithologyOther) {
    this.topSealLithologyOther = topSealLithologyOther;
  }

  public BigDecimal getSealThickness() {
    return sealThickness;
  }

  public void setSealThickness(BigDecimal sealThickness) {
    this.sealThickness = sealThickness;
  }

  public BigDecimal getTopSealFracturePressure() {
    return topSealFracturePressure;
  }

  public void setTopSealFracturePressure(BigDecimal topSealFracturePressure) {
    this.topSealFracturePressure = topSealFracturePressure;
  }
}
