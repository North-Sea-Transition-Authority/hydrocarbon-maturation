package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import java.math.BigDecimal;
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
@Table(name = "mixed_case_outputs")
class MixedCaseOutput {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Boolean hasAssociatedGasRecoverableResources;

  private BigDecimal riskedOilEquivalentRecoverableMean;

  public Boolean getHasAssociatedGasRecoverableResources() {
    return hasAssociatedGasRecoverableResources;
  }

  public void setHasAssociatedGasRecoverableResources(Boolean hasAssociatedGasRecoverableResources) {
    this.hasAssociatedGasRecoverableResources = hasAssociatedGasRecoverableResources;
  }

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

  BigDecimal getRiskedOilEquivalentRecoverableMean() {
    return riskedOilEquivalentRecoverableMean;
  }

  void setRiskedOilEquivalentRecoverableMean(BigDecimal riskedOilEquivalentRecoverableMean) {
    this.riskedOilEquivalentRecoverableMean = riskedOilEquivalentRecoverableMean;
  }
}
