package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Entity
@Table(name = "source_and_seal_details")
class SourceAndSeal {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private String sourceFormationName;

  private String sourceAgeStage;

  private String sourceLithology;

  private String topSealFormationName;

  private String topSealAgeStage;

  private String topSealLithology;

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

  public String getSourceFormationName() {
    return sourceFormationName;
  }

  public void setSourceFormationName(String sourceFormationName) {
    this.sourceFormationName = sourceFormationName;
  }

  public String getSourceAgeStage() {
    return sourceAgeStage;
  }

  public void setSourceAgeStage(String sourceAgeStage) {
    this.sourceAgeStage = sourceAgeStage;
  }

  public String getSourceLithology() {
    return sourceLithology;
  }

  public void setSourceLithology(String sourceLithology) {
    this.sourceLithology = sourceLithology;
  }

  public String getTopSealFormationName() {
    return topSealFormationName;
  }

  public void setTopSealFormationName(String topSealFormationName) {
    this.topSealFormationName = topSealFormationName;
  }

  public String getTopSealAgeStage() {
    return topSealAgeStage;
  }

  public void setTopSealAgeStage(String topSealAgeStage) {
    this.topSealAgeStage = topSealAgeStage;
  }

  public String getTopSealLithology() {
    return topSealLithology;
  }

  public void setTopSealLithology(String topSealLithology) {
    this.topSealLithology = topSealLithology;
  }
}
