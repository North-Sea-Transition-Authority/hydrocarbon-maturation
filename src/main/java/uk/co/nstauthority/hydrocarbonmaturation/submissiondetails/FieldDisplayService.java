package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;

@Service
public class FieldDisplayService {
  public static final String NO_PARENT_FIELD = "No parent field";

  private final FieldPersistenceService fieldPersistenceService;

  @Autowired
  public FieldDisplayService(FieldPersistenceService fieldPersistenceService) {
    this.fieldPersistenceService = fieldPersistenceService;
  }

  public String getParentFieldNameOrNoneFound(Integer parentFieldId) {
    if (parentFieldId == null) {
      return NO_PARENT_FIELD;
    }

    return fieldPersistenceService.getField(parentFieldId)
        .map(Field::getCorrectedLegalFieldName)
        .orElse(NO_PARENT_FIELD);
  }
}
