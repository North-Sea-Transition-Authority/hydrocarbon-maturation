package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.sql.Timestamp;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public class SubmissionQuerySearchRecord {

  private final SubmissionDetailStatus status;
  private final Timestamp approvedTimestamp;
  private final UUID submissionDetailId;
  private final SubmissionType submissionType;

  SubmissionQuerySearchRecord(String status, Timestamp approvedTimestamp,
                              UUID submissionDetailId, String submissionType) {
    this.status = SubmissionDetailStatus.valueOf(status);
    this.approvedTimestamp = approvedTimestamp;
    this.submissionDetailId = submissionDetailId;
    this.submissionType = SubmissionType.valueOf(submissionType);
  }

  SubmissionDetailStatus getStatus() {
    return status;
  }

  Timestamp getApprovedTimestamp() {
    return approvedTimestamp;
  }

  UUID getSubmissionDetailId() {
    return submissionDetailId;
  }

  SubmissionType getSubmissionType() {
    return submissionType;
  }
}
