package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An error has occurred with probability distribution setup")
public class ProbabilityDistributionException extends RuntimeException {

  public ProbabilityDistributionException(String message) {
    super(message);
  }

  public ProbabilityDistributionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProbabilityDistributionException(Throwable cause) {
    super(cause);
  }
}
