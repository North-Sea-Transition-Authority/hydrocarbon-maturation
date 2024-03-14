package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistoryRepository;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistoryService;

@ExtendWith(MockitoExtension.class)
class ProjectStageHistoryServiceTest {
  @Mock
  private ProjectStageHistoryRepository projectStageHistoryRepository;
  @InjectMocks
  private ProjectStageHistoryService projectStageHistoryService;

  private Project project;

  @Test
  void getProjectStageHistories_thenReturnAllHistories() {
    var projectStageHistory1 = new ProjectStageHistory();
    var projectStageHistory2 = new ProjectStageHistory();
    var projectStageHistory3 = new ProjectStageHistory();

    var histories = List.of(projectStageHistory1, projectStageHistory2, projectStageHistory3);
    when(projectStageHistoryRepository.findAllByProject(project))
        .thenReturn(histories);

    assertThat(projectStageHistoryService.getProjectStageHistories(project))
        .isEqualTo(histories);

    verify(projectStageHistoryRepository).findAllByProject(project);
    verifyNoMoreInteractions(projectStageHistoryRepository);
  }

  @Test
  void saveProjectStageHistory_thenVerifyCall() {
    final var STAGE = ProjectStage.CONSENT_GRANTED;
    final var DATE = LocalDate.of(2024, 2, 1);

    projectStageHistoryService.saveProjectStatusHistory(project, STAGE, DATE);

    var captor = ArgumentCaptor.forClass(ProjectStageHistory.class);
    verify(projectStageHistoryRepository).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            ProjectStageHistory::getProject,
            ProjectStageHistory::getStage,
            ProjectStageHistory::getStageDate
        ).containsExactly(
            project,
            STAGE,
            DATE
        );

    verifyNoMoreInteractions(projectStageHistoryRepository);
  }
}