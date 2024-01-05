package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistoryService;

@ExtendWith(MockitoExtension.class)
class FieldStatusServiceTest {
  @Mock
  private FieldStatusHistoryService fieldStatusHistoryService;
  @InjectMocks
  private FieldStatusService fieldStatusService;

  private Field field;

  @BeforeEach
  void setUp() {
    field = new Field();
  }

  @ParameterizedTest
  @EnumSource(FieldStatus.class)
  void getMostRecentFieldStatus_existingHistory_returnsExistingStatus(FieldStatus expectedFieldStatus) {
    var fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistory.setFieldStatus(expectedFieldStatus);

    when(fieldStatusHistoryService.getMostRecentFieldStatusHistory(field))
        .thenReturn(Optional.of(fieldStatusHistory));

    var result = fieldStatusService.getMostRecentFieldStatus(field);

    assertThat(result)
        .isPresent()
        .get()
        .isEqualTo(expectedFieldStatus);
  }

  @Test
  void getMostRecentFieldStatus_noExistingHistory_returnsEmpty() {
    when(fieldStatusHistoryService.getMostRecentFieldStatusHistory(field))
        .thenReturn(Optional.empty());

    var result = fieldStatusService.getMostRecentFieldStatus(field);

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(FieldStatus.class)
  void setMostRecentFieldStatus(FieldStatus fieldStatus) {
    fieldStatusService.setMostRecentFieldStatus(field, fieldStatus);
    verify(fieldStatusHistoryService, times(1)).saveNewFieldStatusHistory(field, fieldStatus);
  }
}