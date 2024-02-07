package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;

@Service
public class FieldStatusHistoryService {
  private final FieldStatusHistoryRepository fieldStatusHistoryRepository;
  private final Clock clock;

  @Autowired
  public FieldStatusHistoryService(FieldStatusHistoryRepository fieldStatusHistoryRepository, Clock clock) {
    this.fieldStatusHistoryRepository = fieldStatusHistoryRepository;
    this.clock = clock;
  }

  public Optional<FieldStatusHistory> getMostRecentFieldStatusHistory(Field field) {
    return fieldStatusHistoryRepository.findAllByField(field)
        .stream()
        .max(Comparator.comparing(FieldStatusHistory::getStatusDate)
            .thenComparing(fieldStatusHistory -> fieldStatusHistory.getFieldStatus().getStatusValue()));
  }

  List<FieldStatusHistory> getAllFieldStatusHistoriesOrderedByDateAndStatus(Field field)  {
    return fieldStatusHistoryRepository.findAllByField(field)
        .stream()
        .sorted(
            Comparator.comparing((FieldStatusHistory fieldStatusHistory1) ->
                    LocalDate.ofInstant(fieldStatusHistory1.getStatusDate(), ZoneId.systemDefault()).atStartOfDay())
                .thenComparing(
                    fieldStatusHistory -> fieldStatusHistory.getFieldStatus().getStatusValue()
                )
        )
        .toList();
  }

  FieldStatusForm fieldStatusHistoryToForm(FieldStatusHistory fieldStatusHistory)  {
    var form  = new FieldStatusForm();

    form.setFieldStatus(fieldStatusHistory.getFieldStatus());

    if (fieldStatusHistory.getStatusDate() != null) {
      var fieldStatusDate = LocalDate.ofInstant(fieldStatusHistory.getStatusDate(), ZoneId.systemDefault());
      form.setFieldStatusDateDay(String.valueOf(fieldStatusDate.getDayOfMonth()));
      form.setFieldStatusDateMonth(fieldStatusDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
      form.setFieldStatusDateYear(String.valueOf(fieldStatusDate.getYear()));
    }

    return form;
  }

  @Transactional
  public void saveNewFieldStatusHistoryFromForm(FieldStatusForm form, Field field) {
    var fieldStatusHistory = new FieldStatusHistory();

    fieldStatusHistory.setField(field);

    fieldStatusHistory.setFieldStatus(form.getFieldStatus());

    if (ObjectUtils.allNotNull(
        form.getFieldStatusDateYear(),
        form.getFieldStatusDateMonth(),
        form.getFieldStatusDateDay())
    ) {
      fieldStatusHistory.setStatusDate(
          LocalDate.of(
                  Integer.parseInt(form.getFieldStatusDateYear()),
                  Integer.parseInt(form.getFieldStatusDateMonth()),
                  Integer.parseInt(form.getFieldStatusDateDay())
          ).atStartOfDay(ZoneId.systemDefault()).toInstant()
      );
    }

    fieldStatusHistoryRepository.save(fieldStatusHistory);
  }

  @Transactional
  public void saveNewFieldStatusHistory(Field field, FieldStatus fieldStatus) {
    var fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistory.setField(field);
    fieldStatusHistory.setFieldStatus(fieldStatus);
    fieldStatusHistory.setStatusDate(clock.instant());

    fieldStatusHistoryRepository.save(fieldStatusHistory);
  }

  public FieldStatusHistory getFieldStatusHistoryOrThrow(UUID fieldStatusHistoryId) {
    return fieldStatusHistoryRepository.findById(fieldStatusHistoryId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no field status history with id %s"
            .formatted(fieldStatusHistoryId)));
  }

  @Transactional
  public void removeFieldStatusHistory(FieldStatusHistory fieldStatusHistory) {
    fieldStatusHistoryRepository.delete(fieldStatusHistory);
  }
}
