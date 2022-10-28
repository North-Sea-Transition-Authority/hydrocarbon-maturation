package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

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
@Table(name = "additional_information")
class AdditionalInformation {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Boolean hasAdditionalComments;
  private String additionalComments;
  private Boolean hasAdditionalDocuments;

  public UUID getId() {
    return id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public Boolean getHasAdditionalComments() {
    return hasAdditionalComments;
  }

  public void setHasAdditionalComments(Boolean hasAdditionalComments) {
    this.hasAdditionalComments = hasAdditionalComments;
  }

  public String getAdditionalComments() {
    return additionalComments;
  }

  public void setAdditionalComments(String additionalComments) {
    this.additionalComments = additionalComments;
  }

  public Boolean getHasAdditionalDocuments() {
    return hasAdditionalDocuments;
  }

  public void setHasAdditionalDocuments(Boolean hasAdditionalDocuments) {
    this.hasAdditionalDocuments = hasAdditionalDocuments;
  }
}
