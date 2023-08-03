package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = PermeabilityController.class)
class PermeabilityControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private PermeabilityFormService permeabilityFormService;

  @MockBean
  private PermeabilityPersistenceService permeabilityPersistenceService;

  @MockBean
  private PermeabilityFormValidator permeabilityFormValidator;


  private UUID submissionDetailId;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;
  private UUID reservoirSubmissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);

    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    reservoirSubmissionDetailId = reservoirSubmissionDetail.getId();
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
  }

  @SecurityTest
  void getPermeabilityPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(permeabilityFormService.getPermeabilityForm(reservoirSubmissionDetail)).thenReturn(new PermeabilityForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(get(ReverseRouter.route(on(PermeabilityController.class)
        .getPermeabilityPage(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/permeability/permeabilityDryWell"));
  }

  @SecurityTest
  void getPermeabilityPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(get(ReverseRouter.route(on(PermeabilityController.class)
            .getPermeabilityPage(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getPermeabilityPage_andTypeIsNotDryWellOrDiscovery_assertForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    mockMvc.perform(get(ReverseRouter.route(on(PermeabilityController.class)
            .getPermeabilityPage(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getPermeabilityPage_andUserNotInResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(get(ReverseRouter.route(on(PermeabilityController.class)
            .getPermeabilityPage(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void savePermeabilityDetails_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(PermeabilityForm.class, "form");
    when(permeabilityFormValidator.isValid(any(), any(), any(), any())).thenReturn(true);

    mockMvc.perform(
        post(ReverseRouter.route(on(PermeabilityController.class)
            .savePermeabilityDetails(submissionDetailId, reservoirSubmissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetailId, null))));

    verify(permeabilityPersistenceService, times(1))
        .savePermeabilityDetail(any(PermeabilityForm.class), eq(reservoirSubmissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void savePermeabilityDetails_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);

    var bindingResult = new BeanPropertyBindingResult(PermeabilityForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(permeabilityFormValidator.isValid(any(), any(), any(), any())).thenReturn(false);

    mockMvc.perform(
        post(ReverseRouter.route(on(PermeabilityController.class)
            .savePermeabilityDetails(submissionDetailId, reservoirSubmissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(permeabilityPersistenceService, never())
        .savePermeabilityDetail(any(), any());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"POST_DRILL_DRY_HOLE", "CREATE_DISCOVERY_FROM_PROSPECT"})
  void getPermeabilityPageModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    var form = new PermeabilityForm();

    when(permeabilityFormService.getPermeabilityForm(reservoirSubmissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(PermeabilityController.class)
                .getPermeabilityPage(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("methodUsed", DisplayableEnumOptionUtil.getDisplayableOptions(AverageMethod.class)),
            entry("source", DisplayableEnumOptionUtil.getDisplayableOptions(PermeabilitySource.class))
        );

    assertEquals(PermeabilityForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetailId)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetailId, null)), ReservoirTaskListController.PAGE_TITLE)
    );
  }
}