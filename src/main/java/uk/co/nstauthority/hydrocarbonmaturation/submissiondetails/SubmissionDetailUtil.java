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

  public static boolean isFieldSubmission(SubmissionType submissionType) {
    return submissionType == SubmissionType.CREATE_FIELD
        || submissionType == SubmissionType.CREATE_FDP
        || submissionType == SubmissionType.CREATE_CSR
        || submissionType == SubmissionType.CREATE_COP
        || submissionType == SubmissionType.CREATE_FDPA;
  }

  public static boolean isFirstSubmission(SubmissionType submissionType) {
    return switch (submissionType) {
      case CREATE_PROSPECT, CREATE_DISCOVERY, CREATE_FDP,
           CREATE_CSR, CREATE_COP, CREATE_FDPA, CREATE_DISCOVERY_FROM_PROSPECT -> true;
      case PROSPECT_UPDATE, POST_DRILL_DRY_HOLE,
          UPDATE_DISCOVERY, RELINQUISHMENT_PROSPECT, RELINQUISHMENT_DISCOVERY, CREATE_FIELD -> false;
    };
  }

  public static boolean isNotFirstSubmissionOrIsFieldSubmission(SubmissionType submissionType) {
    return !isFirstSubmission(submissionType) || isFieldSubmission(submissionType);
  }
}
