package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ExtendWith(MockitoExtension.class)
class ViewFieldStatusHistoryServiceTest {
  private static final LocalDate FIXED_DATE = LocalDate.of(2024, 2, 6);

  @Mock
  private FieldStatusHistoryService fieldStatusHistoryService;
  @InjectMocks
  private ViewFieldStatusHistoryService viewFieldStatusHistoryService;

  private Field field;

  @BeforeEach
  void setUp() {
    field = new Field();
    field.setFieldIdentifier(8001);
  }

  @Test
  void getFieldStatusHistoryViews() {
    var fsh1 = new FieldStatusHistory();
    fsh1.setFieldStatus(FieldStatus.STATUS_200);
    fsh1.setStatusDate(FIXED_DATE);
    fsh1.setField(field);

    var fsh2 = new FieldStatusHistory();
    fsh2.setFieldStatus(FieldStatus.STATUS_400);
    fsh2.setStatusDate(FIXED_DATE.plusDays(1));
    fsh2.setField(field);

    var fsh3 = new FieldStatusHistory();
    fsh3.setFieldStatus(FieldStatus.STATUS_600);
    fsh3.setStatusDate(FIXED_DATE.plusDays(2));
    fsh3.setField(field);

    when(fieldStatusHistoryService.getAllFieldStatusHistoriesOrderedByDateAndStatus(field))
        .thenReturn(List.of(fsh1, fsh2, fsh3));

    assertThat(viewFieldStatusHistoryService.getFieldStatusHistoryViews(field))
        .extracting(
            FieldStatusHistoryDto::fieldStatus,
            FieldStatusHistoryDto::fieldStatusDate,
            FieldStatusHistoryDto::removeUrl
        ).containsExactly(
            Tuple.tuple(
                fsh1.getFieldStatus().getDisplayName(),
                "6 February 2024",
                ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                    .getRemoveFieldStatusConfirmation(
                        fsh1.getField().getFieldIdentifier(),
                        fsh1.getId()))
            ),
            Tuple.tuple(
                fsh2.getFieldStatus().getDisplayName(),
                "7 February 2024",
                ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                    .getRemoveFieldStatusConfirmation(
                        fsh2.getField().getFieldIdentifier(),
                        fsh2.getId()))
            ),
            Tuple.tuple(
                fsh3.getFieldStatus().getDisplayName(),
                "8 February 2024",
                ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                    .getRemoveFieldStatusConfirmation(
                        fsh3.getField().getFieldIdentifier(),
                        fsh3.getId()))
            )
        );
  }

  @Test
  void toFieldStatusDto() {
    var fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistory.setFieldStatus(FieldStatus.STATUS_400);
    fieldStatusHistory.setStatusDate(FIXED_DATE);
    fieldStatusHistory.setId(UUID.randomUUID());
    fieldStatusHistory.setField(field);

    assertThat(ViewFieldStatusHistoryService.toFieldStatusDto(fieldStatusHistory))
        .extracting(
            FieldStatusHistoryDto::fieldStatus,
            FieldStatusHistoryDto::fieldStatusDate,
            FieldStatusHistoryDto::removeUrl
        ).contains(
            fieldStatusHistory.getFieldStatus().getDisplayName(),
            "6 February 2024",
            ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                .getRemoveFieldStatusConfirmation(
                    fieldStatusHistory.getField().getFieldIdentifier(),
                    fieldStatusHistory.getId()))
        );
  }

  private FieldStatusHistory createFieldStatusHistory() {
    var fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistory.setField(field);
    fieldStatusHistory.setId(UUID.randomUUID());

    return fieldStatusHistory;
  }
}