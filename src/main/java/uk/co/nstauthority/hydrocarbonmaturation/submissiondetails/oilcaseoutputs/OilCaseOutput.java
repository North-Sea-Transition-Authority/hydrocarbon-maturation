package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
@Table(name = "oil_case_outputs")
public class OilCaseOutput {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private BigDecimal riskedOilEquivalentRecoverableMean;

  private Boolean hasAssociatedGasRecoverableResources;

  UUID getId() {
    return id;
  }

  void setId(UUID id) {
    this.id = id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public BigDecimal getRiskedOilEquivalentRecoverableMean() {
    return riskedOilEquivalentRecoverableMean;
  }

  public void setRiskedOilEquivalentRecoverableMean(BigDecimal riskedOilEquivalentRecoverableMean) {
    this.riskedOilEquivalentRecoverableMean = riskedOilEquivalentRecoverableMean;
  }

  public Boolean getHasAssociatedGasRecoverableResources() {
    return hasAssociatedGasRecoverableResources;
  }

  public void setHasAssociatedGasRecoverableResources(Boolean hasAssociatedGasRecoverableResources) {
    this.hasAssociatedGasRecoverableResources = hasAssociatedGasRecoverableResources;
  }
}
