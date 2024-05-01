package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter.emptyBindingResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.EnergyPortalConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.AddTeamMemberForm;

@ExtendWith(MockitoExtension.class)
class AreaAddMemberControllerHelperServiceTest {

  @Captor
  private ArgumentCaptor<ModelAndView> modelAndViewCaptor;

  @Mock
  private ControllerHelperService controllerHelperService;

  private AreaAddMemberControllerHelperService areaAddMemberControllerHelperService;

  @BeforeEach
  void setUp() {
    var energyPortalConfiguration = new EnergyPortalConfiguration("registrationUrl", "logoutUrl", "portalLogoutPreSharedKey");
    areaAddMemberControllerHelperService = new AreaAddMemberControllerHelperService(controllerHelperService, energyPortalConfiguration);
  }

  @Test
  void addMemberToTeamSubmission_getAddTeamMemberModelAndView() {

    var form = new AddTeamMemberForm();
    var team = new LegacyTeam();
    var successfulRedirect = new ModelAndView();
    var submitUrl = "submitUrl";
    var backLinkUrl = "backLinkUrl";

    areaAddMemberControllerHelperService.addMemberToTeamSubmission(emptyBindingResult(), form, team, successfulRedirect, submitUrl, backLinkUrl);

    verify(controllerHelperService).checkErrorsAndRedirect(eq(emptyBindingResult()), modelAndViewCaptor.capture(), any());
    var modelAndView = modelAndViewCaptor.getValue();
    assertThat(modelAndView.getViewName()).isEqualTo("remi/permissionmanagement/addTeamMember");
    assertThat(modelAndView.getModel()).containsEntry("htmlTitle", "Add user to %s".formatted(team.getDisplayName()));
    assertThat(modelAndView.getModel()).containsEntry("registrationUrl", "registrationUrl");
    assertThat(modelAndView.getModel()).containsEntry("form", form);
    assertThat(modelAndView.getModel()).containsEntry("submitUrl", submitUrl);
    assertThat(modelAndView.getModel()).containsEntry("backLinkUrl", backLinkUrl);
  }
}