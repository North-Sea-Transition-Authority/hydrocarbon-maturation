package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "related_resources")
public class RelatedResource implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Boolean relatedToAnotherResource;

  @ManyToOne
  @JoinColumn(name = "related_submission_detail_id")
  private SubmissionDetail relatedSubmissionDetail;

  private Boolean preferredView;

  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  @Override
  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public Boolean getRelatedToAnotherResource() {
    return relatedToAnotherResource;
  }

  public void setRelatedToAnotherResource(Boolean relatedToAnotherProspect) {
    this.relatedToAnotherResource = relatedToAnotherProspect;
  }

  public SubmissionDetail getRelatedSubmissionDetail() {
    return relatedSubmissionDetail;
  }

  public void setRelatedSubmissionDetail(SubmissionDetail relatedSubmissionDetail) {
    this.relatedSubmissionDetail = relatedSubmissionDetail;
  }

  public Boolean getPreferredView() {
    return preferredView;
  }

  public void setPreferredView(Boolean preferredView) {
    this.preferredView = preferredView;
  }

  @Override
  public String toString() {
    return "RelatedResource{" +
        "id=" + id +
        ", submissionDetail=" + submissionDetail +
        ", relatedToAnotherResource=" + relatedToAnotherResource +
        ", relatedSubmissionDetail=" + relatedSubmissionDetail +
        ", preferredView=" + preferredView +
        '}';
  }
}
