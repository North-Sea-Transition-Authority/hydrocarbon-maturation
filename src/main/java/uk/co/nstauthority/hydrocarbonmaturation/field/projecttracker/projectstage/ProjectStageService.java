package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistoryService;

@Service
class ProjectStageService {
  private final ProjectStageHistoryService projectStageHistoryService;

  @Autowired
  ProjectStageService(ProjectStageHistoryService projectStageHistoryService) {
    this.projectStageHistoryService = projectStageHistoryService;
  }

  List<ProjectStageHistory> getOrderedProjectStages(Project project) {
    return projectStageHistoryService.getCreatedProjectStageHistories(project)
        .stream()
        .sorted(Comparator.comparing(ProjectStageHistory::getStageDate))
        .toList();
  }

  @Transactional
  public void saveProjectStage(Project project, ProjectStageForm form) {
    var date = LocalDate.of(
        Integer.parseInt(form.getStageDateYear()),
        Integer.parseInt(form.getStageDateMonth()),
        Integer.parseInt(form.getStageDateDay())
    );
    projectStageHistoryService.saveProjectStatusHistory(project, form.getStage(), date);
  }
}
