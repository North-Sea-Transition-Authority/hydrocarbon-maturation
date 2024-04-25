package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.WebMvcConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Import(value = {
    WebMvcConfiguration.class,
    ResourceService.class,
    UndevelopedDiscoveryService.class
})
public abstract class AbstractUndevelopedDiscoveryControllerTest extends AbstractControllerTest {

  @MockBean
  protected UndevelopedDiscoveryService undevelopedDiscoveryService;

  @MockBean
  private PermissionService permissionService;

  @MockBean
  protected ResourceService resourceService;

  protected static final User USER = UserTestUtil.Builder().build();
  protected static final UUID RESOURCE_ID = UUID.randomUUID();
  protected Resource resource;
  protected UndevelopedDiscovery undevelopedDiscovery;

  @BeforeEach
  void setUp() {
    doCallRealMethod().when(userService).getUser();

    resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(resource, Boolean.TRUE);
  }

  protected void setupUserIsReservesAndResourcesAdvisor(User user) {
    when(regulatorTeamService.isRegulatorUser(user))
        .thenReturn(true);
    when(permissionService.hasPermission(user, Set.of(RolePermission.RESERVES_AND_RESOURCES_ANALYST)))
        .thenReturn(true);
  }
}
