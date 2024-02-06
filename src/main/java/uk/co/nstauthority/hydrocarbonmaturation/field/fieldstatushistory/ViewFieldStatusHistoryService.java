package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
class ViewFieldStatusHistoryService {
  private final FieldStatusHistoryService fieldStatusHistoryService;

  @Autowired
  public ViewFieldStatusHistoryService(FieldStatusHistoryService fieldStatusHistoryService) {
    this.fieldStatusHistoryService = fieldStatusHistoryService;
  }

  List<FieldStatusHistoryDto> getFieldStatusHistoryViews(Field field) {
    return fieldStatusHistoryService.getAllFieldStatusHistoriesInOrder(field)
        .stream()
        .map(this::toFieldStatusDto)
        .toList();
  }

  private FieldStatusHistoryDto toFieldStatusDto(FieldStatusHistory fieldStatusHistory) {
    var date = LocalDate.ofInstant(fieldStatusHistory.getStatusDate(), ZoneId.systemDefault());
    var formattedDate = DateUtil.formatDateOrBlank(date);

    return new FieldStatusHistoryDto(fieldStatusHistory.getFieldStatus().getDisplayName(), formattedDate);
  }
}
