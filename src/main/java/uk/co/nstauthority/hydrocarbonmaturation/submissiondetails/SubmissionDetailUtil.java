package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SubmissionDetailUtil {
  private SubmissionDetailUtil() {
  }

  public static String getDefaultForNamelessResource(Instant createdOn) {
    return "Created on %s".formatted(
        LocalDateTime
            .ofInstant(createdOn, ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
    );
  }

  public static boolean isDiscoverySubmission(SubmissionType submissionType) {
    return submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
        || submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY;
  }


  public static boolean isProspectSubmission(SubmissionType submissionType) {
    return submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT;
  }
}
