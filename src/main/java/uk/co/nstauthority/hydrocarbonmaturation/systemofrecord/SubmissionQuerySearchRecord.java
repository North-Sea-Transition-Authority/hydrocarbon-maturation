package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.sql.Timestamp;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public record SubmissionQuerySearchRecord(SubmissionDetailStatus status,
                                          Timestamp approvedTimestamp,
                                          UUID submissionDetailId,
                                          SubmissionType submissionType) {
}
