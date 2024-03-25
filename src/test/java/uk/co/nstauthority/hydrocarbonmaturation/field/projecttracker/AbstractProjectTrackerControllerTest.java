package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.WebMvcConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Import(value = {
    WebMvcConfiguration.class,
    FieldPersistenceService.class,
    PermissionService.class})
public abstract class AbstractProjectTrackerControllerTest extends AbstractControllerTest {
  @MockBean
  protected PermissionService permissionService;

  @MockBean
  protected FieldPersistenceService fieldPersistenceService;

  protected static final User USER = UserTestUtil.Builder().build();
  protected static final UUID RESOURCE_ID = UUID.randomUUID();
  protected static final UUID PROJECT_ID = UUID.randomUUID();
  protected static final Integer FIELD_IDENTIFIER = new Random().nextInt(100) + 8000;
  protected Field field;
  protected Project project;

  @BeforeEach
  void setUp() {
    doCallRealMethod().when(userService).getUser();

    field = FieldTestUtils.createField(RESOURCE_ID, FIELD_IDENTIFIER);
    when(fieldPersistenceService.getFieldOrThrow(RESOURCE_ID))
        .thenReturn(field);

    project = ProjectTestUtils.createProject(field, PROJECT_ID);
    project.setProjectStatus(ProjectStatus.EXTANT);
    when(projectService.getProjectOrThrow(PROJECT_ID))
        .thenReturn(project);
    when(projectService.getProject(PROJECT_ID))
        .thenReturn(Optional.of(project));
  }

  protected void setupUserIsProjectAnalyst(User user) {
    setupUserIsRegulator(user);
    when(permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
  }

  protected void setupUserIsRegulator(User user) {
    when(regulatorTeamService.isRegulatorUser(user))
        .thenReturn(true);
  }

  protected void setupUserIsAreaMember(User user) {
    when(areaTeamService.isAreaUser(user))
        .thenReturn(true);
  }
}
