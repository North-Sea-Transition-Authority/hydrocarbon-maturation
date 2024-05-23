package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ChanceOfSuccessController.class)
class ChanceOfSuccessControllerTest extends AbstractSubmissionControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockBean
  private ChanceOfSuccessFormService chanceOfSuccessFormService;

  @MockBean
  private ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService;

  private static UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "PROSPECT_UPDATE", "CREATE_PROSPECT", "RELINQUISHMENT_PROSPECT",
          "UPDATE_DISCOVERY", "CREATE_DISCOVERY", "CREATE_DISCOVERY_FROM_PROSPECT", "RELINQUISHMENT_DISCOVERY"
      }
  )
  void getChanceOfSuccess_whenIsResponsibleOrganisation_andIsPermittedSubmissionType_assertStatusOk(
      SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);

    when(chanceOfSuccessFormService.getChanceOfSuccessForm(submissionDetail)).thenReturn(new ChanceOfSuccessForm());
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/chanceOfSuccess"));
  }

  @SecurityTest
  void getChanceOfSuccess_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveChanceOfSuccess_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(ChanceOfSuccessController.class).saveChanceOfSuccess(
                submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getChanceOfSuccess_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "PROSPECT_UPDATE", "CREATE_PROSPECT", "RELINQUISHMENT_PROSPECT",
          "UPDATE_DISCOVERY", "CREATE_DISCOVERY", "CREATE_DISCOVERY_FROM_PROSPECT", "RELINQUISHMENT_DISCOVERY"
      },
      mode = EnumSource.Mode.EXCLUDE
  )
  void getChanceOfSuccess_andTypeIsNotProspectOrDiscovery_assertForbidden(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            get(ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "PROSPECT_UPDATE", "CREATE_PROSPECT", "RELINQUISHMENT_PROSPECT",
          "UPDATE_DISCOVERY", "CREATE_DISCOVERY", "CREATE_DISCOVERY_FROM_PROSPECT", "RELINQUISHMENT_DISCOVERY"
      },
      mode = EnumSource.Mode.EXCLUDE
  )
  void saveChanceOfSuccess_andTypeIsNotProspectOrDiscovery_assertForbidden(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);

    mockMvc.perform(
            post(ReverseRouter.route(on(ChanceOfSuccessController.class).saveChanceOfSuccess(
                submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveChanceOfSuccess_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(ChanceOfSuccessForm.class, "form");
    when(chanceOfSuccessFormService.validate(any(), any(),
        any(), eq(submissionDetail))).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(
                on(ChanceOfSuccessController.class).saveChanceOfSuccess(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(chanceOfSuccessFormService, times(1)).validate(any(), any(), eq(validationType), eq(submissionDetail));
    verify(chanceOfSuccessPersistenceService, times(1)).saveChanceOfSuccess(any(ChanceOfSuccessForm.class),
        eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveChanceOfSuccess_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    var bindingResult = new BeanPropertyBindingResult(ChanceOfSuccessForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(chanceOfSuccessFormService.validate(any(), any(), any(), eq(submissionDetail))).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(
                on(ChanceOfSuccessController.class).saveChanceOfSuccess(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(chanceOfSuccessFormService, times(1)).validate(any(), any(),
        eq(validationType), eq(submissionDetail));
    verify(chanceOfSuccessPersistenceService, never()).saveChanceOfSuccess(any(), any());
  }

  @Test
  void getChanceOfSuccessModelAndView_prospect_assertModelProperties() throws Exception {
    var form = new ChanceOfSuccessForm();

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(chanceOfSuccessFormService.getChanceOfSuccessForm(submissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(submissionDetailId)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap).contains(
        entry("form", form),
        entry("pageTitle", ChanceOfSuccessController.PROSPECT_PAGE_NAME)
    );

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)),
            TaskListController.PAGE_NAME)
    );
  }

  @Test
  void getChanceOfSuccessModelAndView_discovery_assertModelProperties() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var form = new ChanceOfSuccessForm();

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(chanceOfSuccessFormService.getChanceOfSuccessForm(submissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(submissionDetailId)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap).contains(
        entry("form", form),
        entry("pageTitle", ChanceOfSuccessController.DISCOVERY_PAGE_NAME)
    );

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)),
            TaskListController.PAGE_NAME)
    );
  }
}