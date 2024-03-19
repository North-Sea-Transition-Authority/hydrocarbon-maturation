package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;

@Service
public class ProjectTrackerControllerService {
  private final ProjectService projectService;

  @Autowired
  public ProjectTrackerControllerService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public Project getProjectForResourceOrThrow(UUID resourceId, UUID projectId) {
    var project = projectService.getProjectOrThrow(projectId);

    if (!resourceId.equals(project.getField().getResource().getId())) {
      throw new RemiEntityNotFoundException("There is no resource with id %s related to the project with id %s"
          .formatted(resourceId, projectId));
    }
    return project;
  }
}
