package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ExtendWith(MockitoExtension.class)
class SubmissionTypeSelectServiceTest {

  private static SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator;
  private static SubmissionTypeSelectService submissionTypeSelectService;
  private static UserDetailService userDetailService;
  private static PermissionService permissionService;


  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @BeforeAll
  static void setup() {
    submissionTypeSelectFormValidator = mock(SubmissionTypeSelectFormValidator.class);
    userDetailService = mock(UserDetailService.class);
    permissionService = mock(PermissionService.class);
    submissionTypeSelectService = new SubmissionTypeSelectService(submissionTypeSelectFormValidator,
        userDetailService, permissionService);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionTypeSelectService.validate(form, bindingResult);
    verify(submissionTypeSelectFormValidator, times(1)).validate(form, bindingResult);
  }

  @Test
  void getSubmissionTypeOptions_allPermissions() {
    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(eq(USER), any())).thenReturn(true);

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY))
        .containsOnlyKeys(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT.name(), SubmissionType.CREATE_DISCOVERY.name(),
            SubmissionType.UPDATE_DISCOVERY.name(), SubmissionType.RELINQUISHMENT_DISCOVERY.name());
  }

  @Test
  void getDiscoveryPurposeTypeOptions_onlyAreaTeamPermissions() {
    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY))
        .containsOnlyKeys(SubmissionType.UPDATE_DISCOVERY.name(), SubmissionType.RELINQUISHMENT_DISCOVERY.name());
  }
}