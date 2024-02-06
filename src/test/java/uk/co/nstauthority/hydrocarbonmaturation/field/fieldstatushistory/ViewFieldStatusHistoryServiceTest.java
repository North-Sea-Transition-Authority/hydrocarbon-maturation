package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;

@ExtendWith(MockitoExtension.class)
class ViewFieldStatusHistoryServiceTest {
  @Mock
  private FieldStatusHistoryService fieldStatusHistoryService;
  @InjectMocks
  private ViewFieldStatusHistoryService viewFieldStatusHistoryService;

  @Test
  void getFieldStatusHistoryViews() {

    var fixedInstant1 = Instant.parse("2024-01-01T00:00:00.00Z");
    var fsh1 = new FieldStatusHistory();
    fsh1.setFieldStatus(FieldStatus.STATUS_200);
    fsh1.setStatusDate(fixedInstant1);

    var fixedInstant2 = Instant.parse("2024-02-02T00:00:00.00Z");
    var fsh2 = new FieldStatusHistory();
    fsh2.setFieldStatus(FieldStatus.STATUS_400);
    fsh2.setStatusDate(fixedInstant2);

    var field = new Field();

    when(fieldStatusHistoryService.getAllFieldStatusHistoriesInOrder(field))
        .thenReturn(List.of(fsh1,  fsh2));

    assertThat(viewFieldStatusHistoryService.getFieldStatusHistoryViews(field))
        .extracting(
            FieldStatusHistoryDto::fieldStatus,
            FieldStatusHistoryDto::fieldStatusDate
        ).containsExactly(
            Tuple.tuple(fsh1.getFieldStatus().getDisplayName(), "1 Jan 2024"),
            Tuple.tuple(fsh2.getFieldStatus().getDisplayName(), "2 Feb 2024")
        );
  }
}