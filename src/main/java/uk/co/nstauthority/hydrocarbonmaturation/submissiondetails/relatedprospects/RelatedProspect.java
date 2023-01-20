package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedprospects;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "related_prospects")
class RelatedProspect implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Boolean relatedToAnotherProspect;

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

  public Boolean getRelatedToAnotherProspect() {
    return relatedToAnotherProspect;
  }

  public void setRelatedToAnotherProspect(Boolean relatedToAnotherProspect) {
    this.relatedToAnotherProspect = relatedToAnotherProspect;
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
    return "RelatedProspect{" +
        "id=" + id +
        ", submissionDetail=" + submissionDetail +
        ", relatedToAnotherProspect=" + relatedToAnotherProspect +
        ", relatedSubmissionDetail=" + relatedSubmissionDetail +
        ", preferredView=" + preferredView +
        '}';
  }
}
