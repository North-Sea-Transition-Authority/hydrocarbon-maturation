package uk.co.nstauthority.hydrocarbonmaturation.field.epmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;

@Component
@WebEndpoint(id = "field-status-message")
public class FieldStatusActuatorController {

  private final FieldStatusSnsService fieldStatusSnsService;
  private final FieldStatusService fieldStatusService;
  private final FieldPersistenceService fieldPersistenceService;

  @Autowired
  public FieldStatusActuatorController(
      FieldStatusSnsService fieldStatusSnsService,
      FieldStatusService fieldStatusService,
      FieldPersistenceService fieldPersistenceService
  ) {
    this.fieldStatusSnsService = fieldStatusSnsService;
    this.fieldStatusService = fieldStatusService;
    this.fieldPersistenceService = fieldPersistenceService;
  }

  @WriteOperation
  ResponseEntity<Void> publishMessage(@Selector Integer fieldId) throws ResponseStatusException {
    var field = fieldPersistenceService.getField(fieldId);

    if (field.isPresent()) {
      var status = fieldStatusService.getMostRecentFieldStatus(field.get());

      if (status.isPresent()) {
        fieldStatusSnsService.publishFieldStatusUpdateSnsMessage(fieldId, status.get().getStatusValue());
      } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Current status could not be found for field %s".formatted(fieldId));
      }
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The field (id: %s) does not exist".formatted(fieldId));
    }

    return ResponseEntity.ok().build();
  }

}
