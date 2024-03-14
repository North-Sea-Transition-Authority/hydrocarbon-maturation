package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.ProjectStage;

@Service
public class ProjectStageHistoryService {
  private final ProjectStageHistoryRepository projectStageHistoryRepository;

  @Autowired
  ProjectStageHistoryService(ProjectStageHistoryRepository projectStageHistoryRepository) {
    this.projectStageHistoryRepository = projectStageHistoryRepository;
  }

  public Collection<ProjectStageHistory> getProjectStageHistories(Project project) {
    return projectStageHistoryRepository.findAllByProject(project);
  }

  public Optional<ProjectStageHistory> getProjectStageHistory(UUID id) {
    return projectStageHistoryRepository.findById(id);
  }

  @Transactional
  public void removeProjectStage(ProjectStageHistory projectStageHistory) {
    projectStageHistoryRepository.delete(projectStageHistory);
  }

  @Transactional
  public void saveProjectStatusHistory(Project project, ProjectStage stage, LocalDate stageDate) {
    var projectStageHistory = new ProjectStageHistory();
    projectStageHistory.setProject(project);
    projectStageHistory.setStage(stage);
    projectStageHistory.setStageDate(stageDate);

    projectStageHistoryRepository.save(projectStageHistory);
  }
}
