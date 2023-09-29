package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An error has occurred with generating the summary")
public class SummaryException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 3949052771162140614L;

  public SummaryException(String message) {
    super(message);
  }

  public SummaryException(String message, Throwable cause) {
    super(message, cause);
  }

}