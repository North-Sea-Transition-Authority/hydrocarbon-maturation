package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails;

import java.time.Instant;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;

public class ProjectTestUtils {
  private ProjectTestUtils() {
  };

  public static Project createPopulatedProject(String projectName,
                                               ProjectType projectType,
                                               ProjectStatus projectStatus,
                                               Boolean isPartOfPipeline,
                                               Instant createdTimestamp) {
    var project = new Project();

    project.setId(UUID.randomUUID());
    var field = FieldTestUtils.createField(8000);
    project.setField(field);

    project.setProjectName(projectName);
    project.setProjectType(projectType);
    project.setProjectStatus(projectStatus);
    project.setPartOfPipeline(isPartOfPipeline);
    project.setCreatedTimestamp(createdTimestamp);

    return project;
  }

  public static Project createProject(Field field, UUID projectId) {
    var project = new Project();

    project.setId(projectId);
    project.setField(field);
    project.setProjectName("Project name");
    project.setProjectType(ProjectType.CONSENT_GRANTED);

    return project;
  }
}
