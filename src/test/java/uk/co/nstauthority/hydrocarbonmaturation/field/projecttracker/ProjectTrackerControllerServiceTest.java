package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;

@ExtendWith(MockitoExtension.class)
class ProjectTrackerControllerServiceTest {
  @Mock
  private ProjectService projectService;
  @InjectMocks
  private ProjectTrackerControllerService projectTrackerControllerService;

  private final UUID RESOURCE_ID = UUID.randomUUID();
  private final UUID PROJECT_ID = UUID.randomUUID();
  private final UUID UNRELATED_ID = UUID.randomUUID();

  private Project project;

  @BeforeEach
  void setUp() {
    var FIELD_IDENTIFIER = 8000;
    var field = FieldTestUtils.createField(RESOURCE_ID, FIELD_IDENTIFIER);

    project = ProjectTestUtils.createProject(field, PROJECT_ID);
  }

  @Test
  void getProjectForResourceOrThrow_whenProjectDoesNotExist_thenThrow() {
    when(projectService.getProjectOrThrow(PROJECT_ID))
        .thenThrow(RemiEntityNotFoundException.class);

    assertThatThrownBy(() -> projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .isInstanceOf(RemiEntityNotFoundException.class);
  }

  @Test
  void getProjectForResourceOrThrow_whenProjectAndResourceDoNotMatch_thenThrow() {
    when(projectService.getProjectOrThrow(PROJECT_ID))
        .thenReturn(project);

    assertThatThrownBy(() -> projectTrackerControllerService.getProjectForResourceOrThrow(UNRELATED_ID, PROJECT_ID))
        .isInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("There is no resource with id %s related to the project with id %s"
            .formatted(UNRELATED_ID, PROJECT_ID));
  }

  @Test
  void getProjectForResourceOrThrow_whenProjectAndResourceMatch_thenReturnProject() {
    when(projectService.getProjectOrThrow(PROJECT_ID))
        .thenReturn(project);

    assertThat(projectTrackerControllerService.getProjectForResourceOrThrow(RESOURCE_ID, PROJECT_ID))
        .isEqualTo(project);
  }
}