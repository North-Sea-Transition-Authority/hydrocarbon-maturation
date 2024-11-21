package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.UuidGenerator;
import uk.co.fivium.digitalnotificationlibrary.core.notification.DomainReference;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionType;

@Entity
@Table(name = "submission_details")
public class SubmissionDetail implements DomainReference {

  @Id
  @UuidGenerator
  private UUID id;

  @JoinColumn(name = "submission_id")
  @ManyToOne
  private Submission submission;

  private Instant createdTimestamp;

  private Integer createdByWuaId;

  private Integer submittedByWuaId;

  private Integer acceptedByWuaId;

  private Instant submittedTimestamp;

  private Instant acceptedTimestamp;

  @Enumerated(EnumType.STRING)
  private SubmissionDetailStatus status;

  @Enumerated(EnumType.STRING)
  private SubmissionType submissionType;

  private Integer versionNumber;

  @Enumerated(EnumType.STRING)
  private SubmitterType submitterType;

  private Boolean correction;

  @Enumerated(EnumType.STRING)
  private CorrectionType correctionType;

  private Integer correctionByWuaId;

  private Instant correctionTimestamp;

  private Integer legacyResDataId;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Instant getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Instant createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public Integer getCreatedByWuaId() {
    return createdByWuaId;
  }

  public void setCreatedByWuaId(Integer createdByWuaId) {
    this.createdByWuaId = createdByWuaId;
  }

  public SubmissionDetailStatus getStatus() {
    return status;
  }

  public void setStatus(SubmissionDetailStatus status) {
    this.status = status;
  }

  public SubmissionType getSubmissionType() {
    return submissionType;
  }

  public void setSubmissionType(SubmissionType submissionType) {
    this.submissionType = submissionType;
  }

  public Integer getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(Integer versionNumber) {
    this.versionNumber = versionNumber;
  }

  public Integer getSubmittedByWuaId() {
    return submittedByWuaId;
  }

  public void setSubmittedByWuaId(Integer submittedByWuaId) {
    this.submittedByWuaId = submittedByWuaId;
  }

  public Integer getAcceptedByWuaId() {
    return acceptedByWuaId;
  }

  public void setAcceptedByWuaId(Integer acceptedByWuaId) {
    this.acceptedByWuaId = acceptedByWuaId;
  }

  public Instant getSubmittedTimestamp() {
    return submittedTimestamp;
  }

  public void setSubmittedTimestamp(Instant submittedTimestamp) {
    this.submittedTimestamp = submittedTimestamp;
  }

  public Instant getAcceptedTimestamp() {
    return acceptedTimestamp;
  }

  public void setAcceptedTimestamp(Instant acceptedTimestamp) {
    this.acceptedTimestamp = acceptedTimestamp;
  }

  public SubmitterType getSubmitterType() {
    return submitterType;
  }

  public void setSubmitterType(SubmitterType submitterType) {
    this.submitterType = submitterType;
  }

  public Boolean isCorrection() {
    return correction;
  }

  public void setCorrection(Boolean correction) {
    this.correction = correction;
  }

  public CorrectionType getCorrectionType() {
    return correctionType;
  }

  public void setCorrectionType(
      CorrectionType correctionType) {
    this.correctionType = correctionType;
  }

  public Integer getCorrectionByWuaId() {
    return correctionByWuaId;
  }

  public void setCorrectionByWuaId(Integer correctionByWuaId) {
    this.correctionByWuaId = correctionByWuaId;
  }

  public Instant getCorrectionTimestamp() {
    return correctionTimestamp;
  }

  public void setCorrectionTimestamp(Instant correctionTimestamp) {
    this.correctionTimestamp = correctionTimestamp;
  }

  public boolean isProspectSubmission() {
    return SubmissionDetailUtil.isProspectSubmission(submissionType);
  }

  public boolean isDryWellOrDiscoverySubmission() {
    return submissionType == SubmissionType.POST_DRILL_DRY_HOLE
        || isDiscoverySubmission();
  }

  public boolean isDiscoverySubmission() {
    return SubmissionDetailUtil.isDiscoverySubmission(submissionType);
  }

  public boolean isFieldSubmission() {
    return SubmissionDetailUtil.isFieldSubmission(submissionType);
  }

  public boolean isConceptSelectReportSubmission() {
    return submissionType.equals(SubmissionType.CREATE_CSR);
  }

  public boolean isFieldDevelopmentPlanSubmission() {
    return submissionType.equals(SubmissionType.CREATE_FDP);
  }

  public boolean isFieldDevelopmentPlanAddendumSubmission() {
    return submissionType.equals(SubmissionType.CREATE_FDPA);
  }

  public boolean isCessationOfProductionSubmission() {
    return submissionType.equals(SubmissionType.CREATE_COP);
  }

  public boolean isApprovedOrSubmittedSubmission() {
    return status == SubmissionDetailStatus.SUBMITTED
        || status == SubmissionDetailStatus.ACCEPTED;
  }

  public boolean isDraftOrSubmittedSubmission() {
    return status == SubmissionDetailStatus.DRAFT
        || status == SubmissionDetailStatus.SUBMITTED;
  }

  public boolean isDraftOrApprovedSubmission() {
    return status == SubmissionDetailStatus.DRAFT
        || status == SubmissionDetailStatus.ACCEPTED;
  }

  public boolean isProspectRelinquishment() {
    return submissionType == SubmissionType.RELINQUISHMENT_PROSPECT;
  }

  public boolean isRelinquishment() {
    return isProspectRelinquishment() || isDiscoveryRelinquishment();
  }

  public boolean isDiscoveryRelinquishment() {
    return submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY;
  }

  public boolean isDiscoveryFromProspect() {
    return submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT;
  }

  public boolean isDeletedOrWithdrawnSubmission() {
    return status == SubmissionDetailStatus.DELETED || BooleanUtils.isTrue(submission.isWithdrawn());
  }

  public boolean isDryWellSubmission() {
    return submissionType == SubmissionType.POST_DRILL_DRY_HOLE;
  }

  public boolean isInitialResourceSubmission() {
    return submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT;
  }

  public boolean isFirstSubmission() {
    return SubmissionDetailUtil.isFirstSubmission(submissionType);
  }

  public boolean isSubmissionType(SubmissionType submissionType) {
    return submissionType.equals(this.submissionType);
  }

  public boolean hasSameSubmissionType(SubmissionDetail otherSubmissionDetail) {
    return submissionType == otherSubmissionDetail.getSubmissionType();
  }

  public boolean hasSameResourceType(SubmissionDetail otherSubmissionDetail) {
    return submission.getResource().getType() == otherSubmissionDetail.getSubmission().getResource().getType();
  }

  public boolean isUpdateToSubmission() {
    return versionNumber != null && versionNumber > 1;
  }

  public Submission getSubmission() {
    return submission;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }

  public Integer getLegacyResDataId() {
    return legacyResDataId;
  }

  public void setLegacyResDataId(Integer legacyResDataId) {
    this.legacyResDataId = legacyResDataId;
  }

  @Override
  public String getDomainId() {
    return String.valueOf(id);
  }

  @Override
  public String getDomainType() {
    return "SUBMISSION_DETAIL";
  }
}
