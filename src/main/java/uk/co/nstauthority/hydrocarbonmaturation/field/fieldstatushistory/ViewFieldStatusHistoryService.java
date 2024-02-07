package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class ViewFieldStatusHistoryService {
  private final FieldStatusHistoryService fieldStatusHistoryService;

  @Autowired
  public ViewFieldStatusHistoryService(FieldStatusHistoryService fieldStatusHistoryService) {
    this.fieldStatusHistoryService = fieldStatusHistoryService;
  }

  List<FieldStatusHistoryDto> getFieldStatusHistoryViews(Field field) {
    return fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(field)
        .stream()
        .map(ViewFieldStatusHistoryService::toFieldStatusDto)
        .toList();
  }

  public static FieldStatusHistoryDto toFieldStatusDto(FieldStatusHistory fieldStatusHistory) {
    var date = LocalDate.ofInstant(fieldStatusHistory.getStatusDate(), ZoneId.systemDefault());
    var formattedDate = DateUtil.formatLongDate(date);

    var removeUrl = ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
        .getRemoveFieldStatusConfirmation(
            fieldStatusHistory.getField().getFieldIdentifier(),
            fieldStatusHistory.getId()));

    return new FieldStatusHistoryDto(
        removeUrl,
        fieldStatusHistory.getFieldStatus().getDisplayName(),
        formattedDate
    );
  }
}
