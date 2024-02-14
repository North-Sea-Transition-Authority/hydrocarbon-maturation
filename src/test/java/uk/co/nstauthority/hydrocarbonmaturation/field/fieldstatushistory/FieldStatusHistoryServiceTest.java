package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;

@ExtendWith(MockitoExtension.class)
class FieldStatusHistoryServiceTest {
  @Mock
  private FieldStatusHistoryRepository fieldStatusHistoryRepository;
  @Mock
  private Clock clock;
  @InjectMocks
  private FieldStatusHistoryService fieldStatusHistoryService;

  private Field field;

  @BeforeEach
  void setUp() {
    field = new Field();
  }

  @Test
  void getMostRecentFieldStatusHistory_noFieldStatusHistory_returnEmpty() {
    when(fieldStatusHistoryRepository.findAllByField(field))
        .thenReturn(Set.of());
    assertThat(fieldStatusHistoryService.getMostRecentFieldStatusHistory(field))
        .isEmpty();
  }

  @Test
  void getMostRecentFieldStatusHistory_sameDate_returnHigherStatus() {
    LocalDate date = LocalDate.now();

    var fieldStatusHistory1 = new FieldStatusHistory();
    fieldStatusHistory1.setFieldStatus(FieldStatus.STATUS_200);
    fieldStatusHistory1.setStatusDate(date);

    var fieldStatusHistory2 = new FieldStatusHistory();
    fieldStatusHistory2.setFieldStatus(FieldStatus.STATUS_300);
    fieldStatusHistory2.setStatusDate(date);

    var field = new Field();
    when(fieldStatusHistoryRepository.findAllByField(field))
        .thenReturn(Set.of(fieldStatusHistory1, fieldStatusHistory2));

    assertThat(fieldStatusHistoryService.getMostRecentFieldStatusHistory(field))
        .isPresent()
        .get()
        .isEqualTo(fieldStatusHistory2);
  }

  @Test
  void getMostRecentFieldStatusHistory_sameStatus_returnMostRecent() {
    LocalDate date1 = LocalDate.now();
    var fieldStatusHistory1 = new FieldStatusHistory();
    fieldStatusHistory1.setFieldStatus(FieldStatus.STATUS_200);
    fieldStatusHistory1.setStatusDate(date1);

    LocalDate date2 = date1.plusDays(1);
    var fieldStatusHistory2 = new FieldStatusHistory();
    fieldStatusHistory2.setFieldStatus(FieldStatus.STATUS_200);
    fieldStatusHistory2.setStatusDate(date2);

    var field = new Field();
    when(fieldStatusHistoryRepository.findAllByField(field))
        .thenReturn(Set.of(fieldStatusHistory1, fieldStatusHistory2));

    assertThat(fieldStatusHistoryService.getMostRecentFieldStatusHistory(field))
        .isPresent()
        .contains(fieldStatusHistory2);
  }

  @Test
  void getAllFieldStatusHistoriesOrderedByDateAndStatus_ordersByDate() {
    LocalDate fixedInstant = LocalDate.now();

    var oldestFieldStatusHistory = new FieldStatusHistory();
    oldestFieldStatusHistory.setStatusDate(fixedInstant);
    var olderFieldStatusHistory = new FieldStatusHistory();
    olderFieldStatusHistory.setStatusDate(fixedInstant.plusDays(5));
    var newestFieldStatusHistory = new FieldStatusHistory();
    newestFieldStatusHistory.setStatusDate(fixedInstant.plusDays(10));

    when(fieldStatusHistoryRepository.findAllByField(field))
        .thenReturn(Set.of(newestFieldStatusHistory, olderFieldStatusHistory, oldestFieldStatusHistory));

    assertThat(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(field))
        .isEqualTo(List.of(oldestFieldStatusHistory, olderFieldStatusHistory, newestFieldStatusHistory));
  }

  @Test
  void getAllFieldStatusHistoriesOrderedByDateAndStatus_ordersByStatus() {
    LocalDate fixedInstant = LocalDate.now();

    var lowestStatusHistory = new FieldStatusHistory();
    lowestStatusHistory.setStatusDate(fixedInstant);
    lowestStatusHistory.setFieldStatus(FieldStatus.STATUS_300);
    var statusHistory = new FieldStatusHistory();
    statusHistory.setStatusDate(fixedInstant);
    statusHistory.setFieldStatus(FieldStatus.STATUS_400);
    var highestStatusHistory = new FieldStatusHistory();
    highestStatusHistory.setStatusDate(fixedInstant);
    highestStatusHistory.setFieldStatus(FieldStatus.STATUS_600);

    when(fieldStatusHistoryRepository.findAllByField(field))
        .thenReturn(Set.of(statusHistory, lowestStatusHistory, highestStatusHistory));

    assertThat(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(field))
        .isEqualTo(List.of(lowestStatusHistory, statusHistory, highestStatusHistory));
  }

  @Test
  void fieldStatusHistoryToForm_emptyFieldStatusHistory() {
    var fieldStatusHistory = new FieldStatusHistory();
    var form = fieldStatusHistoryService.fieldStatusHistoryToForm(fieldStatusHistory);
    assertThat(form)
        .extracting(
            FieldStatusForm::getFieldStatus,
            FieldStatusForm::getFieldStatusDateDay,
            FieldStatusForm::getFieldStatusDateMonth,
            FieldStatusForm::getFieldStatusDateYear
        ).containsOnlyNulls();
  }

  @Test
  void fieldStatusHistoryToForm_assertForm() {
    LocalDate fieldStatusDate = LocalDate.of(2024, 2, 2);

    var fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistory.setStatusDate(fieldStatusDate);
    fieldStatusHistory.setFieldStatus(FieldStatus.STATUS_200);

    var form = fieldStatusHistoryService.fieldStatusHistoryToForm(fieldStatusHistory);
    assertThat(form)
        .extracting(
            FieldStatusForm::getFieldStatus,
            FieldStatusForm::getFieldStatusDateDay,
            FieldStatusForm::getFieldStatusDateMonth,
            FieldStatusForm::getFieldStatusDateYear
        ).contains(
          fieldStatusHistory.getFieldStatus(),
          "2",
          "February",
          "2024"
        );
  }

  @Test
  void saveNewFieldStatusHistory() {
    var fieldStatus = FieldStatus.STATUS_200;

    var fixedInstant = Instant.parse("2024-02-02T00:00:00.00Z");
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());

    var captor = ArgumentCaptor.forClass(FieldStatusHistory.class);
    fieldStatusHistoryService.saveNewFieldStatusHistory(field, fieldStatus);
    verify(fieldStatusHistoryRepository, times(1)).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            FieldStatusHistory::getField,
            FieldStatusHistory::getFieldStatus,
            FieldStatusHistory::getStatusDate
        ).containsExactly(
            field,
            fieldStatus,
            LocalDate.of(2024, 2, 2)
        );
  }

  @Test
  void saveNewFieldStatusHistoryFromForm() {
    var form = new FieldStatusForm();
    form.setFieldStatus(FieldStatus.STATUS_400);
    form.setFieldStatusDateDay("02");
    form.setFieldStatusDateMonth("02");
    form.setFieldStatusDateYear("2024");

    var captor = ArgumentCaptor.forClass(FieldStatusHistory.class);
    fieldStatusHistoryService.saveNewFieldStatusHistoryFromForm(form, field);
    verify(fieldStatusHistoryRepository, times(1)).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            FieldStatusHistory::getField,
            FieldStatusHistory::getFieldStatus,
            FieldStatusHistory::getStatusDate
        ).containsExactly(
            field,
            form.getFieldStatus(),
            LocalDate.of(2024, 2, 2)
        );
  }

  @Test
  void getFieldStatusHistoryOrThrow_noFieldStatusWithId_thenThrow() {
    var uuid = UUID.randomUUID();

    when(fieldStatusHistoryRepository.findById(uuid))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> fieldStatusHistoryService.getFieldStatusHistoryOrThrow(uuid))
        .isInstanceOf(RemiEntityNotFoundException.class);
  }

  @Test
  void getFieldStatusHistoryOrThrow_fieldStatusExists_thenReturned() {
    var uuid = UUID.randomUUID();
    var fieldStatusHistory = new FieldStatusHistory();

    when(fieldStatusHistoryRepository.findById(uuid))
        .thenReturn(Optional.of(fieldStatusHistory));

    assertThat(fieldStatusHistoryService.getFieldStatusHistoryOrThrow(uuid))
        .isEqualTo(fieldStatusHistory);
  }

  @Test
  void removeFieldStatusHistory() {
    var fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistoryService.removeFieldStatusHistory(fieldStatusHistory);

    verify(fieldStatusHistoryRepository).delete(fieldStatusHistory);
    verifyNoMoreInteractions(fieldStatusHistoryRepository);
  }
}