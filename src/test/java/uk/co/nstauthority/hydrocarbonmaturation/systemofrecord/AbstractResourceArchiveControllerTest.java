package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.WebMvcConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Import(value= {
    WebMvcConfiguration.class,
    ResourceArchiveService.class
})
public abstract class AbstractResourceArchiveControllerTest extends AbstractControllerTest {

  protected static final User USER = UserTestUtil.Builder().build();
  protected static final UUID RESOURCE_ID = UUID.randomUUID();
  protected static final ResponsibleTeamType RESPONSIBLE_TEAM_TYPE = ResponsibleTeamType.ONSHORE;
  protected static final ResourceRecordSummaryView RESOURCE_RECORD = new ResourceRecordSummaryView(
      ResourceType.PROSPECT,
      UUID.randomUUID(),
      "resource name",
      "coi",
      "licence app ref",
      null,
      null,
      null,
      null,
      RESPONSIBLE_TEAM_TYPE,
      null,
      null,
      null
  );

  protected Resource resource;

  @MockBean
  protected ResourceService resourceService;

  @MockBean
  protected ResourceArchiveService resourceArchiveService;

  @MockBean
  protected ViewResourceRecordService viewResourceRecordService;

  @MockBean
  protected ResponsibleTeamService responsibleTeamService;

  @BeforeEach
  void setUp() {
    doCallRealMethod().when(userService).getUser();

    resource = new Resource();
    resource.setId(RESOURCE_ID);
  }

  protected void setUpResourceRecords() {
    when(resourceService.getResourceOrThrow(RESOURCE_ID))
        .thenReturn(resource);
    when(viewResourceRecordService.getResourceRecordOrThrow(RESOURCE_ID))
        .thenReturn(RESOURCE_RECORD);
  }

  protected void setUpUserRolesWithoutResponsibleTeam() {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(true);
  }

  protected void setUpUserInResponsibleTeam() {
    setUpUserRolesWithoutResponsibleTeam();
    setUpResourceRecords();
    when(responsibleTeamService.isInResponsibleTeam(USER, RESPONSIBLE_TEAM_TYPE))
        .thenReturn(true);
  }
}
