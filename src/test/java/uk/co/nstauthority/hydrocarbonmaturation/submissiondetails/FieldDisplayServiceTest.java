package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;

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
}