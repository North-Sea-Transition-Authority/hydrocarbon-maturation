package uk.co.nstauthority.hydrocarbonmaturation.exception;

import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The entity could not be found")
public class RemiEntityNotFoundException extends EntityNotFoundException {

  public RemiEntityNotFoundException(String message) {
    super(message);
  }

}
