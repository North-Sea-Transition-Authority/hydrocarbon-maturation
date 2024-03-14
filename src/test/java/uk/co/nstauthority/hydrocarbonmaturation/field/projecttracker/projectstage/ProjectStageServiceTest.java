package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistoryService;

@ExtendWith(MockitoExtension.class)
class ProjectStageServiceTest {
  @Mock
  private ProjectStageHistoryService projectStageHistoryService;
  @InjectMocks
  private ProjectStageService projectStageService;

  private Project project;

  @Nested
  class GivenGetOrderedProjectStages {
    private static final LocalDate FIXED_DATE = LocalDate.of(2024, Month.MARCH, 4);

    private ProjectStageHistory firstCreatedStageHistory;
    private ProjectStageHistory secondCreatedStageHistory;
    private ProjectStageHistory lastCreatedStageHistory;

    @BeforeEach
    void setUp() {
      firstCreatedStageHistory  = new ProjectStageHistory();
      firstCreatedStageHistory.setStage(ProjectStage.CONSENT_GRANTED);
      firstCreatedStageHistory.setStageDate(FIXED_DATE.plusDays(1));

      secondCreatedStageHistory = new ProjectStageHistory();
      secondCreatedStageHistory.setStage(ProjectStage.AWAITING_OPERATOR_INFO);
      secondCreatedStageHistory.setStageDate(FIXED_DATE.plusMonths(1));

      lastCreatedStageHistory = new ProjectStageHistory();
      lastCreatedStageHistory.setStage(ProjectStage.FDP_TECHNICAL_COMMENTS_WITH_REGULATOR);
      lastCreatedStageHistory.setStageDate(FIXED_DATE.plusYears(1));
    }

    @Test
    void whenNoHistories_thenReturnEmptyList() {
      assertThat(projectStageService.getOrderedProjectStages(project))
          .isEmpty();
    }

    @Test
    void whenOneHistory_thenListWithDto() {
      when(projectStageHistoryService.getCreatedProjectStageHistories(project))
          .thenReturn(List.of(firstCreatedStageHistory));

      assertThat(projectStageService.getOrderedProjectStages(project))
          .extracting(
              ProjectStageHistory::getStage,
              ProjectStageHistory::getStageDate
          ).containsExactly(
              Tuple.tuple(
                  firstCreatedStageHistory.getStage(),
                  firstCreatedStageHistory.getStageDate()
              )
          );
    }

    @Test
    void whenMultipleHistories_thenListOrderedByDate() {
      when(projectStageHistoryService.getCreatedProjectStageHistories(project))
          .thenReturn(List.of(lastCreatedStageHistory, firstCreatedStageHistory, secondCreatedStageHistory));

      assertThat(projectStageService.getOrderedProjectStages(project))
          .extracting(
              ProjectStageHistory::getStage,
              ProjectStageHistory::getStageDate
          ).containsExactly(
              Tuple.tuple(
                  firstCreatedStageHistory.getStage(),
                  firstCreatedStageHistory.getStageDate()
              ),
              Tuple.tuple(
                  secondCreatedStageHistory.getStage(),
                  secondCreatedStageHistory.getStageDate()
              ),
              Tuple.tuple(
                  lastCreatedStageHistory.getStage(),
                  lastCreatedStageHistory.getStageDate()
              )
          );
    }
  }

  @Test
  void saveProjectStage() {
    var form = new ProjectStageForm();
    form.setStageDateDay("1");
    form.setStageDateMonth("3");
    form.setStageDateYear("2024");

    projectStageService.saveProjectStage(project, form);

    verify(projectStageHistoryService).saveProjectStatusHistory(project, form.getStage(), LocalDate.of(2024, 3, 1));
    verifyNoMoreInteractions(projectStageHistoryService);
  }
}