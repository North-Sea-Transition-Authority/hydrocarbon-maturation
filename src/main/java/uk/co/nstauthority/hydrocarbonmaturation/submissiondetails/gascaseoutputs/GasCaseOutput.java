package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

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
@Table(name = "gas_case_outputs")
class GasCaseOutput {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Boolean hasAssociatedCondensateRecoverableResources;

  UUID getId() {
    return id;
  }

  void setId(UUID id) {
    this.id = id;
  }

  SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  Boolean getHasAssociatedCondensateRecoverableResources() {
    return hasAssociatedCondensateRecoverableResources;
  }

  void setHasAssociatedCondensateRecoverableResources(Boolean hasAssociatedCondensateRecoverableResources) {
    this.hasAssociatedCondensateRecoverableResources = hasAssociatedCondensateRecoverableResources;
  }
}
