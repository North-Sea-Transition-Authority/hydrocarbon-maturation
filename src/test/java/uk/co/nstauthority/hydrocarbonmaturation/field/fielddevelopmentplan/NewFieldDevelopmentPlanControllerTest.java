package uk.co.nstauthority.hydrocarbonmaturation.field.fielddevelopmentplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSelectForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSelectValidator;
import uk.co.nstauthority.hydrocarbonmaturation.field.NewSubmissionFromFieldService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = NewFieldDevelopmentPlanController.class)
class NewFieldDevelopmentPlanControllerTest extends AbstractSubmissionControllerTest {

  @MockitoBean
  private FieldSelectValidator fieldSelectValidator;

  @MockitoBean
  private NewSubmissionFromFieldService newSubmissionFromFieldService;

  @BeforeEach
  void setUp() {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(true);
  }

  @SecurityTest
  void getNewFdpPage_whenNoPermission_assertForbidden() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(false);


    mockMvc.perform(
            get(ReverseRouter.route(on(NewFieldDevelopmentPlanController.class).getNewFdpPage(null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getNewFdpPage_assertModelProperties() throws Exception {
    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                get(ReverseRouter.route(on(NewFieldDevelopmentPlanController.class).getNewFdpPage(null)))
                    .with(user(USER)))
            .andReturn()
            .getModelAndView()
        )
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("actionUrl",
                ReverseRouter.route(on(NewFieldDevelopmentPlanController.class).createNewFdp(null, null, null))),
            entry("backLinkUrl", ReverseRouter.route(on(StartFieldDevelopmentPlanController.class).getStartPage())),
            entry("fieldApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class).searchFields(null, SubmissionType.CREATE_FDP, null)))
        );
  }

  @SecurityTest
  void createdNewFdp_whenInvalidForm_assertStatusIsOk() throws Exception {
    when(fieldSelectValidator.isValid(any(BindingResult.class))).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewFieldDevelopmentPlanController.class)
                .createNewFdp(null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(newSubmissionFromFieldService, never()).createSubmissionFromField(any(FieldSelectForm.class), any(SubmissionType.class), any(User.class));
  }

  @Test
  void createdNewFdp_whenValidForm_assertSaveAndRedirect() throws Exception {
    when(fieldSelectValidator.isValid(any(BindingResult.class))).thenReturn(true);
    var submissionDetail = new SubmissionDetail();
    when(newSubmissionFromFieldService.createSubmissionFromField(any(FieldSelectForm.class), eq(SubmissionType.CREATE_FDP), eq(USER)))
        .thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewFieldDevelopmentPlanController.class)
                .createNewFdp(null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId(), null))));

    verify(newSubmissionFromFieldService, times(1))
        .createSubmissionFromField(any(FieldSelectForm.class), eq(SubmissionType.CREATE_FDP), eq(USER));
  }
}