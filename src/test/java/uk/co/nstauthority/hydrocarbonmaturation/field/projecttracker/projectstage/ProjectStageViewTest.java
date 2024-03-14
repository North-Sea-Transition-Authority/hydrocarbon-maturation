package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;

@ExtendWith(MockitoExtension.class)
class ProjectStageViewTest {
  private final static LocalDate FIXED_DATE = LocalDate.of(2024, 11, 3);
  private final static String FIXED_DATE_DISPLAY = "3 November 2024";

  @Test
  void from_assertBuiltCorrectly() {
    var projectStageHistory = new ProjectStageHistory();
    projectStageHistory.setStage(ProjectStage.CONSENT_GRANTED);
    projectStageHistory.setStageDate(FIXED_DATE);

    assertThat(ProjectStageView.from(projectStageHistory))
        .extracting(
            ProjectStageView::stage,
            ProjectStageView::date
        )
        .contains(
            ProjectStage.CONSENT_GRANTED.getDisplayName(),
            FIXED_DATE_DISPLAY
        );
  }
}