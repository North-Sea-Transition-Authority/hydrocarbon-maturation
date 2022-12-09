package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private TeamMemberService teamMemberService;

  @InjectMocks
  private PermissionService permissionService;

  @Test
  void hasPermission_whenTeamMemberNull_thenFalse() {
    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(null);
    assertFalse(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)));
  }

  @Test
  void hasPermission_whenTeamMemberEmpty_thenFalse() {
    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(Collections.emptyList());
    assertFalse(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)));
  }

  @Test
  void hasPermission_whenTeamMemberWithNoMatchingPermission_thenFalse() {

    var teamMember = TeamMemberTestUtil.Builder()
        .withRole(TestTeamRole.NON_CREATE_NOMINATION_ROLE)
        .build();

    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(List.of(teamMember));

    assertFalse(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)));
  }

  @Test
  void hasPermission_whenTeamMemberWithMatchingPermission_thenTrue() {

    var teamMember = TeamMemberTestUtil.Builder()
        .withRole(TestTeamRole.CREATE_NOMINATION_ROLE)
        .build();

    when(teamMemberService.getUserAsTeamMembers(USER)).thenReturn(List.of(teamMember));

    assertTrue(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)));
  }

  enum TestTeamRole implements TeamRole {

    CREATE_NOMINATION_ROLE(RolePermission.EXPLORATION),
    NON_CREATE_NOMINATION_ROLE(RolePermission.VIEW_SYSTEM_OF_RECORD);

    private final RolePermission rolePermission;

    TestTeamRole(RolePermission rolePermission) {
      this.rolePermission = rolePermission;
    }

    @Override
    public String getDescription() {
      return null;
    }

    @Override
    public int getDisplayOrder() {
      return 0;
    }

    @Override
    public String getDisplayName() {
      return null;
    }

    @Override
    public Set<RolePermission> getRolePermissions() {
      return Set.of(rolePermission);
    }
  }

}
