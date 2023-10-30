package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals;

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
@Table(name = "combined_case_volumetric_totals")
public class CombinedCaseVolumetricTotal implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Boolean hasAssociatedCondensateRecoverableResources;

  public UUID getId() {
    return id;
  }

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

  public Boolean getHasAssociatedCondensateRecoverableResources() {
    return hasAssociatedCondensateRecoverableResources;
  }

  public void setHasAssociatedCondensateRecoverableResources(Boolean hasAssociatedCondensateRecoverableResources) {
    this.hasAssociatedCondensateRecoverableResources = hasAssociatedCondensateRecoverableResources;
  }
}
