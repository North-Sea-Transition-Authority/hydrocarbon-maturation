package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@Entity
@Table(name = "submission_details")
public class SubmissionDetail {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @JoinColumn(name = "submission_id")
  @ManyToOne
  private Submission submission;

  private Instant createdTimestamp;

  private Integer createdByWuaId;

  private Integer submittedByWuaId;

  private Integer withdrawnByWuaId;

  private Integer approvedByWuaId;

  private Instant submittedTimestamp;

  private Instant approvedTimestamp;

  private Instant withdrawnTimestamp;

  @Enumerated(EnumType.STRING)
  private SubmissionDetailStatus status;

  @Enumerated(EnumType.STRING)
  private SubmissionType submissionType;

  private Integer versionNumber;

  @Enumerated(EnumType.STRING)
  private SubmitterType submitterType;

  private String withdrawalReason;

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

  public Integer getApprovedByWuaId() {
    return approvedByWuaId;
  }

  public void setApprovedByWuaId(Integer approvedByWuaId) {
    this.approvedByWuaId = approvedByWuaId;
  }

  public Instant getSubmittedTimestamp() {
    return submittedTimestamp;
  }

  public void setSubmittedTimestamp(Instant submittedTimestamp) {
    this.submittedTimestamp = submittedTimestamp;
  }

  public Instant getApprovedTimestamp() {
    return approvedTimestamp;
  }

  public void setApprovedTimestamp(Instant approvedTimestamp) {
    this.approvedTimestamp = approvedTimestamp;
  }

  public SubmitterType getSubmitterType() {
    return submitterType;
  }

  public void setSubmitterType(SubmitterType submitterType) {
    this.submitterType = submitterType;
  }

  public boolean isProspectSubmission() {
    return submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT;
  }

  public boolean isDryWellOrDiscoverySubmission() {
    return submissionType == SubmissionType.POST_DRILL_DRY_HOLE
        || isDiscoverySubmission();
  }

  public boolean isDiscoverySubmission() {
    return submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
        || submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY;
  }

  public boolean isApprovedOrSubmittedSubmission() {
    return status == SubmissionDetailStatus.SUBMITTED
        || status == SubmissionDetailStatus.APPROVED;
  }

  public boolean isDraftOrSubmittedSubmission() {
    return status == SubmissionDetailStatus.DRAFT
        || status == SubmissionDetailStatus.SUBMITTED;
  }

  public boolean isDraftOrApprovedSubmission() {
    return status == SubmissionDetailStatus.DRAFT
        || status == SubmissionDetailStatus.APPROVED;
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
    return status == SubmissionDetailStatus.DELETED || status == SubmissionDetailStatus.WITHDRAWN;
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
    return submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.CREATE_DISCOVERY;
  }

  public boolean hasSameSubmissionType(SubmissionDetail otherSubmissionDetail) {
    return submissionType == otherSubmissionDetail.getSubmissionType();
  }

  public boolean isUpdateSubmission() {
    return versionNumber > 1;
  }

  public Integer getWithdrawnByWuaId() {
    return withdrawnByWuaId;
  }

  public void setWithdrawnByWuaId(Integer withdrawnByWuaId) {
    this.withdrawnByWuaId = withdrawnByWuaId;
  }

  public Instant getWithdrawnTimestamp() {
    return withdrawnTimestamp;
  }

  public void setWithdrawnTimestamp(Instant withdrawnTimestamp) {
    this.withdrawnTimestamp = withdrawnTimestamp;
  }

  public String getWithdrawalReason() {
    return withdrawalReason;
  }

  public void setWithdrawalReason(String withdrawalReason) {
    this.withdrawalReason = withdrawalReason;
  }

  public Submission getSubmission() {
    return submission;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }
}
