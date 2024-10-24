package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ExtendWith(MockitoExtension.class)
class FieldDisplayServiceTest {
  @Mock
  private FieldPersistenceService fieldPersistenceService;
  @InjectMocks
  private FieldDisplayService fieldDisplayService;

  @Test
  void getParentFieldNameOrNoneFound_nullParentId() {
    Integer parentId = null;

    assertThat(fieldDisplayService.getParentFieldNameOrNoneFound(parentId))
        .isEqualTo(FieldDisplayService.NO_PARENT_FIELD);
  }

  @Test
  void getParentFieldNameOrNoneFound_noFieldWithId() {
    var parentId = 404;

    when(fieldPersistenceService.getField(parentId))
        .thenReturn(Optional.empty());

    assertThat(fieldDisplayService.getParentFieldNameOrNoneFound(parentId))
        .isEqualTo(FieldDisplayService.NO_PARENT_FIELD);
  }

  @Test
  void getParentFieldNameOrNoneFound_fieldWithIdExists_returnsCorrectedLegalFieldNAme() {
    var parentId = 302;

    var field = new Field();
    field.setCorrectedLegalFieldName("correct legal name");
    when(fieldPersistenceService.getField(parentId))
        .thenReturn(Optional.of(field));

    assertThat(fieldDisplayService.getParentFieldNameOrNoneFound(parentId))
        .isEqualTo(field.getCorrectedLegalFieldName());
  }

  @Test
  void createFieldFamilyView_determinedField_noSubareas() {
    var field = new Field();

    when(fieldPersistenceService.getFieldsByParentDeterminedField(field)).thenReturn(List.of());

    assertThat(fieldDisplayService.createFieldFamilyView(field))
        .usingRecursiveComparison()
        .isEqualTo(new FieldFamilyView(
          "",
          "",
          Map.of(),
          Map.of()
        )
    );
  }

  @Test
  void createFieldFamilyView_determinedField_hasSubareas() {
    var field = new Field();

    var subAreaResource = new Resource();
    subAreaResource.setId(UUID.randomUUID());

    var subAreaField = new Field();
    subAreaField.setResource(subAreaResource);
    subAreaField.setCorrectedLegalFieldName("Name");

    when(fieldPersistenceService.getFieldsByParentDeterminedField(field)).thenReturn(List.of(subAreaField));

    assertThat(fieldDisplayService.createFieldFamilyView(field))
        .usingRecursiveComparison()
        .isEqualTo(new FieldFamilyView(
                "",
                "",
                Map.of(),
                Map.of(subAreaField.getCorrectedLegalFieldName(),
                    ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(subAreaResource.getId(), null))
                )
            )
        );
  }

  @Test
  void createFieldFamilyView_subareaField() {
    var parentResource = new Resource();
    parentResource.setId(UUID.randomUUID());

    var parentField = new Field();
    parentField.setResource(parentResource);
    parentField.setCorrectedLegalFieldName("Parent name");

    var resource = new Resource();
    resource.setId(UUID.randomUUID());

    var field = new Field();
    field.setFieldIdentifier(1);
    field.setResource(resource);
    field.setCorrectedLegalFieldName("Field name");
    field.setParentDeterminedField(parentField);

    var siblingResource = new Resource();
    siblingResource.setId(UUID.randomUUID());

    var siblingField = new Field();
    siblingField.setFieldIdentifier(2);
    siblingField.setResource(siblingResource);
    siblingField.setCorrectedLegalFieldName("Sibling name");

    when(fieldPersistenceService.getFieldsByParentDeterminedField(parentField)).thenReturn(List.of(field, siblingField));

    assertThat(fieldDisplayService.createFieldFamilyView(field)).usingRecursiveComparison()
        .isEqualTo(new FieldFamilyView(
        parentField.getCorrectedLegalFieldName(),
        ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(parentResource.getId(), null)),
        Map.of(siblingField.getCorrectedLegalFieldName(),
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(siblingResource.getId(), null))),
        Map.of()
      )
    );
  }

}