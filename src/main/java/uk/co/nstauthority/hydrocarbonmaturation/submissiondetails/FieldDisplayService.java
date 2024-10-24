package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

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

  public FieldFamilyView createFieldFamilyView(Field field) {
    var parentField = field.getParentDeterminedField();

    if (parentField != null) {
      var siblingFields = fieldPersistenceService.getFieldsByParentDeterminedField(parentField);

      var siblingFieldMap = siblingFields.stream()
          .filter(f -> !f.getFieldIdentifier().equals(field.getFieldIdentifier()))
          .sorted(Comparator.comparing(Field::getCorrectedLegalFieldName))
          .collect(StreamUtil.toLinkedHashMap(
            Field::getCorrectedLegalFieldName,
            f -> ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(f.getResource().getId(), null))
      ));

      return new FieldFamilyView(
          parentField.getCorrectedLegalFieldName(),
          ReverseRouter.route(on(ViewResourceRecordController.class)
              .showResourceRecord(parentField.getResource().getId(), null)),
         siblingFieldMap,
         Map.of()
      );
    }

    var childFields = fieldPersistenceService.getFieldsByParentDeterminedField(field);
    var childFieldMap = childFields.stream()
        .sorted(Comparator.comparing(Field::getCorrectedLegalFieldName))
        .collect(StreamUtil.toLinkedHashMap(
          Field::getCorrectedLegalFieldName,
          f -> ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(f.getResource().getId(), null))
    ));

    return new FieldFamilyView(
        "",
        "",
        Map.of(),
        childFieldMap
    );
  }
}
