package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import java.time.Clock;
import java.util.Comparator;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  @Transactional
  public void saveNewFieldStatusHistory(Field field, FieldStatus fieldStatus) {
    var fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistory.setField(field);
    fieldStatusHistory.setFieldStatus(fieldStatus);
    fieldStatusHistory.setStatusDate(clock.instant());

    fieldStatusHistoryRepository.save(fieldStatusHistory);
  }
}
