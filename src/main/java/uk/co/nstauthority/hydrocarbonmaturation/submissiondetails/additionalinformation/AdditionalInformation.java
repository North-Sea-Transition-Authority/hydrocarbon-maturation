package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "additional_information")
public class AdditionalInformation implements SubmissionDetailChildEntity {

  @Id
  @UuidGenerator
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

  public void setId(UUID id) {
    this.id = id;
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
