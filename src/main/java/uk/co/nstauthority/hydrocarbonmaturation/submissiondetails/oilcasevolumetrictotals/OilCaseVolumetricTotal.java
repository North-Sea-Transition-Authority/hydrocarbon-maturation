package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals;

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
@Table(name = "oil_case_volumetric_totals")
public class OilCaseVolumetricTotal implements SubmissionDetailChildEntity {

  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Boolean useIndustryStandardConversion;

  private Boolean fieldVolumetricInformationCorrect;

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

  public Boolean getUseIndustryStandardConversion() {
    return useIndustryStandardConversion;
  }

  public void setUseIndustryStandardConversion(Boolean useIndustryStandardConversion) {
    this.useIndustryStandardConversion = useIndustryStandardConversion;
  }

  public Boolean getFieldVolumetricInformationCorrect() {
    return fieldVolumetricInformationCorrect;
  }

  public void setFieldVolumetricInformationCorrect(Boolean fieldVolumetricInformationCorrect) {
    this.fieldVolumetricInformationCorrect = fieldVolumetricInformationCorrect;
  }
}
