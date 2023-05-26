package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;

@ExtendWith(MockitoExtension.class)
class AreaAddRolesControllerHelperServiceTest {

  @Mock
  private EnergyPortalUserService energyPortalUserService;

  @InjectMocks
  private AreaAddRolesControllerHelperService areaAddRolesControllerHelperService;

  private final WebUserAccountId wuaId = new WebUserAccountId(42L);

  @Test
  void renderAddTeamMemberRoles() {
    var roles = Map.of("role", "description");
    var backLinkUrl = "backLinkUrl";
    var energyPortalUserDto = new EnergyPortalUserDto(42L, "title", "forname", "surname",
        "", "", false, true);

    when(energyPortalUserService.findByWuaId(wuaId)).thenReturn(Optional.of(energyPortalUserDto));

    var modelAndView = areaAddRolesControllerHelperService
        .renderAddTeamMemberRoles(wuaId, roles, backLinkUrl);
    assertThat(modelAndView.getViewName()).isEqualTo("remi/permissionmanagement/addTeamMemberRoles");
    assertThat(modelAndView.getModel()).containsEntry("pageTitle", "What actions does %s perform?".formatted(energyPortalUserDto.displayName()));
    assertThat(modelAndView.getModel()).containsEntry("roles", roles);
    assertThat(modelAndView.getModel()).containsEntry("backLinkUrl", backLinkUrl);
  }

  @Test
  void getEnergyPortalUser_notFound() {

    when(energyPortalUserService.findByWuaId(wuaId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> areaAddRolesControllerHelperService
        .getEnergyPortalUser(wuaId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("""
                      404 NOT_FOUND "No Energy Portal user with WUA_ID 42 could be found""");
  }

  @Test
  void getEnergyPortalUser_sharedAccount() {

    var energyPortalUserDto = new EnergyPortalUserDto(42L, "title", "forname", "surname",
        "", "", true, true);

    when(energyPortalUserService.findByWuaId(wuaId)).thenReturn(Optional.of(energyPortalUserDto));

    assertThatThrownBy(() -> areaAddRolesControllerHelperService
        .getEnergyPortalUser(wuaId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("""
         400 BAD_REQUEST "Energy Portal user with WUA_ID 42 is a shared account and is not allowed to be added to this service""");
  }

  @Test
  void getEnergyPortalUser_cantLogin() {

    var energyPortalUserDto = new EnergyPortalUserDto(42L, "title", "forname", "surname",
        "", "", false, false);

    when(energyPortalUserService.findByWuaId(wuaId)).thenReturn(Optional.of(energyPortalUserDto));

    assertThatThrownBy(() -> areaAddRolesControllerHelperService
        .getEnergyPortalUser(wuaId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("""
                  400 BAD_REQUEST "    Energy Portal user with WUA_ID 42 does not have login access to the Energy Portal""");
  }
}