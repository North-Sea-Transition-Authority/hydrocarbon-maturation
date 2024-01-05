package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.Comparator;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistoryService;

@Service
public class FieldStatusService {
  private final FieldStatusHistoryService fieldStatusHistoryService;

  @Autowired
  public FieldStatusService(FieldStatusHistoryService fieldStatusHistoryService) {
    this.fieldStatusHistoryService = fieldStatusHistoryService;
  }

  public Optional<FieldStatus> getMostRecentFieldStatus(Field field) {
    return fieldStatusHistoryService.getMostRecentFieldStatusHistory(field)
        .stream()
        .map(FieldStatusHistory::getFieldStatus)
        .max(Comparator.comparing(FieldStatus::getStatusValue));
  }

  @Transactional
  public void setMostRecentFieldStatus(Field field, FieldStatus fieldStatus) {
    fieldStatusHistoryService.saveNewFieldStatusHistory(field, fieldStatus);
  }
}
