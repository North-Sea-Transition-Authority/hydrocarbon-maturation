package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellRestController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = DryWellDetailsController.class)
class DryWellDetailsControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private DryWellDetailsFormService dryWellDetailsFormService;

  @MockBean
  private DryWellDetailsPersistenceService dryWellDetailsPersistenceService;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
  }

  @SecurityTest
  void getDryWellDetails_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail)).thenReturn(new DryWellDetailsForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(
        get(ReverseRouter.route(on(DryWellDetailsController.class).getDryWellDetails(submissionDetailId, null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/wellDrillDetails/dryWellDetails"));
  }

  @SecurityTest
  void getDryWellDetails_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(
            get(ReverseRouter.route(on(DryWellDetailsController.class).getDryWellDetails(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getDryWellDetails_andTypeIsNotPostDrill_assertForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    mockMvc.perform(
            get(ReverseRouter.route(on(DryWellDetailsController.class).getDryWellDetails(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveDryWellDetails_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(DryWellDetailsForm.class, "form");
    when(dryWellDetailsFormService.validate(any(), any(), any(), eq(submissionDetail))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DryWellDetailsController.class)
            .saveDryWellDetails(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(dryWellDetailsFormService, times(1)).validate(
        any(), any(), eq(validationType), eq(submissionDetail));
    verify(dryWellDetailsPersistenceService, times(1))
        .saveDryWellDetails(any(DryWellDetailsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveDryWellDetailsPage_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    var bindingResult = new BeanPropertyBindingResult(DryWellDetailsForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(dryWellDetailsFormService.validate(any(), any(), any(), eq(submissionDetail))).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DryWellDetailsController.class)
            .saveDryWellDetails(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(dryWellDetailsFormService, times(1)).validate(
        any(), any(), eq(validationType), eq(submissionDetail));
    verify(dryWellDetailsPersistenceService, never()).saveDryWellDetails(any(), any());
  }

  @Test
  void getDryWellDetailsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var form = new DryWellDetailsForm();
    var preselectedWell = Map.of("well1", "well1");

    when(dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail)).thenReturn(form);
    when(dryWellDetailsFormService.getPreSelectedWell(form.getDrillDetailsForm().getWell())).thenReturn(preselectedWell);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(DryWellDetailsController.class)
                .getDryWellDetails(submissionDetailId, null)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("datumTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirDatum.class)),
            entry("dryHoleTypes", DisplayableEnumOptionUtil.getDisplayableOptions(DryHoleType.class)),
            entry("sourceFailureCategories",
                DisplayableEnumOptionUtil.getDisplayableOptions(SourceFailureCategory.class)),
            entry("reservoirFailureCategories",
            DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirFailureCategory.class)),
            entry("sealFailureCategories",
            DisplayableEnumOptionUtil.getDisplayableOptions(SealFailureCategory.class)),
            entry("trapFailureCategories",
            DisplayableEnumOptionUtil.getDisplayableOptions(TrapFailureCategory.class)),
            entry("migrationFailureCategories",
            DisplayableEnumOptionUtil.getDisplayableOptions(MigrationFailureCategory.class)),
            entry("wellEndpoint",
                SearchSelectorService.route(on(WellRestController.class).searchWonsWells(null))),
            entry("well", preselectedWell)
        );

    assertEquals(DryWellDetailsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)),
            TaskListController.PAGE_NAME)
    );
  }
}