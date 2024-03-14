package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory;

import java.time.LocalDate;
import java.util.List;
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

  public List<ProjectStageHistory> getCreatedProjectStageHistories(Project project) {
    return projectStageHistoryRepository.findAllByProject(project)
        .stream().filter(projectStageHistory ->
            ProjectStageStatus.CREATED.equals(projectStageHistory.getProjectStageStatus()))
        .toList();
  }

  public Optional<ProjectStageHistory> getProjectStageHistory(UUID id) {
    return projectStageHistoryRepository.findById(id);
  }

  @Transactional
  public void removeProjectStage(ProjectStageHistory projectStageHistory) {
    projectStageHistory.setProjectStageStatus(ProjectStageStatus.REMOVED);
    projectStageHistoryRepository.save(projectStageHistory);
  }

  @Transactional
  public void saveProjectStatusHistory(Project project, ProjectStage stage, LocalDate stageDate) {
    var projectStageHistory = new ProjectStageHistory();
    projectStageHistory.setProject(project);
    projectStageHistory.setStage(stage);
    projectStageHistory.setStageDate(stageDate);

    projectStageHistory.setProjectStageStatus(ProjectStageStatus.CREATED);

    projectStageHistoryRepository.save(projectStageHistory);
  }
}
