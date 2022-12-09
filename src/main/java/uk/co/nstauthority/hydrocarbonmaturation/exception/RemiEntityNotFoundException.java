package uk.co.nstauthority.hydrocarbonmaturation.exception;

import java.io.Serial;
import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The entity could not be found")
public class RemiEntityNotFoundException extends EntityNotFoundException {

  @Serial
  private static final long serialVersionUID = 4443343368848518168L;

  public RemiEntityNotFoundException(String message) {
    super(message);
  }

}
