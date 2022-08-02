package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

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
@Table(name = "opportunity_details")
class OpportunityDetail {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  @Enumerated(EnumType.STRING)
  private OpportunityType opportunityType;

  private String otherTypeOfOpportunity;

  private String opportunityComments;

  private String keyEvaluationWorkCarriedOut;

  private String keyWorkRequiredForFurtherMaturation;

  public OpportunityDetail() {
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

  public OpportunityType getOpportunityType() {
    return opportunityType;
  }

  public void setOpportunityType(
      OpportunityType opportunityType) {
    this.opportunityType = opportunityType;
  }

  public String getOtherTypeOfOpportunity() {
    return otherTypeOfOpportunity;
  }

  public void setOtherTypeOfOpportunity(String otherTypeOfOpportunity) {
    this.otherTypeOfOpportunity = otherTypeOfOpportunity;
  }

  public String getOpportunityComments() {
    return opportunityComments;
  }

  public void setOpportunityComments(String opportunityComments) {
    this.opportunityComments = opportunityComments;
  }

  public String getKeyEvaluationWorkCarriedOut() {
    return keyEvaluationWorkCarriedOut;
  }

  public void setKeyEvaluationWorkCarriedOut(String keyEvaluationWorkCarriedOut) {
    this.keyEvaluationWorkCarriedOut = keyEvaluationWorkCarriedOut;
  }

  public String getKeyWorkRequiredForFurtherMaturation() {
    return keyWorkRequiredForFurtherMaturation;
  }

  public void setKeyWorkRequiredForFurtherMaturation(String keyWorkRequiredForFurtherMaturation) {
    this.keyWorkRequiredForFurtherMaturation = keyWorkRequiredForFurtherMaturation;
  }

  @Override
  public String toString() {
    return "OpportunityDetail{" +
        "id=" + id +
        ", submissionDetail=" + submissionDetail +
        ", opportunityType=" + opportunityType +
        ", otherTypeOfOpportunity='" + otherTypeOfOpportunity + '\'' +
        ", opportunityComments='" + opportunityComments + '\'' +
        ", keyEvaluationWorkCarriedOut='" + keyEvaluationWorkCarriedOut + '\'' +
        ", keyWorkRequiredForFurtherMaturation='" + keyWorkRequiredForFurtherMaturation + '\'' +
        '}';
  }
}
