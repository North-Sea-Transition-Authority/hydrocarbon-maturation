package uk.co.nstauthority.hydrocarbonmaturation.field.epmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;

@Component
@Endpoint(id = "field-updated-message")
public class FieldUpdateActuatorController {

  private final FieldSnsService fieldSnsService;
  private final FieldPersistenceService fieldPersistenceService;

  @Autowired
  public FieldUpdateActuatorController(
      FieldSnsService fieldSnsService,
      FieldPersistenceService fieldPersistenceService
  ) {
    this.fieldSnsService = fieldSnsService;
    this.fieldPersistenceService = fieldPersistenceService;
  }

  @WriteOperation
  ResponseEntity<Void> publishMessage(@Selector Integer fieldId) throws ResponseStatusException {
    var field = fieldPersistenceService.getField(fieldId);

    if (field.isPresent()) {
      fieldSnsService.publishFieldUpdatedSnsMessage(field.get());
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The field (id: %s) does not exist".formatted(fieldId));
    }

    return ResponseEntity.ok().build();
  }

}
