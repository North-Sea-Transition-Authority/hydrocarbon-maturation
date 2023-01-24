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
}
