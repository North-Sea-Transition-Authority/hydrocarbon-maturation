package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.Comparator;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.field.epmq.FieldStatusSnsService;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistoryService;

@Service
public class FieldStatusService {
  private final FieldStatusHistoryService fieldStatusHistoryService;
  private final FieldStatusSnsService fieldStatusSnsService;

  @Autowired
  public FieldStatusService(FieldStatusHistoryService fieldStatusHistoryService,
                            FieldStatusSnsService fieldStatusSnsService
  ) {
    this.fieldStatusHistoryService = fieldStatusHistoryService;
    this.fieldStatusSnsService = fieldStatusSnsService;
  }

  public Optional<FieldStatus> getMostRecentFieldStatus(Field field) {
    return fieldStatusHistoryService.getMostRecentFieldStatusHistory(field)
        .stream()
        .max(Comparator.comparing(FieldStatusHistory::getStatusDate)
            .thenComparing(FieldStatusHistory::getFieldStatus))
        .map(FieldStatusHistory::getFieldStatus);
  }

  @Transactional
  public void setMostRecentFieldStatus(Field field, FieldStatus fieldStatus) {
    fieldStatusHistoryService.saveNewFieldStatusHistory(field, fieldStatus);
    fieldStatusSnsService.publishFieldStatusUpdateSnsMessage(field.getFieldIdentifier(), fieldStatus.getStatusValue());
  }
}
