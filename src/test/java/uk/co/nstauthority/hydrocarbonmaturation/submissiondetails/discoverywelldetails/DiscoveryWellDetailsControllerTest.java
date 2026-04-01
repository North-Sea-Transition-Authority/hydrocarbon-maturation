package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = DiscoveryWellDetailsController.class)
class DiscoveryWellDetailsControllerTest extends AbstractSubmissionControllerTest {

  @MockitoBean
  private DiscoveryWellDetailsFormService discoveryWellDetailsFormService;

  @MockitoBean
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @MockitoBean
  private FormationSearchService formationSearchService;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
  }

  @SecurityTest
  void getDiscoveryWellDetails_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail)).thenReturn(new DiscoveryWellDetailsForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryWellDetailsController.class).getDiscoveryWellDetails(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/wellDrillDetails/discoveryWellDrillDetails"));
  }

  @SecurityTest
  void getDiscoveryWellDetails_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryWellDetailsController.class).getDiscoveryWellDetails(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getDiscoveryWellDetails_andTypeIsNotDiscovery_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryWellDetailsController.class).getDiscoveryWellDetails(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getDiscoveryWellDetails_andNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryWellDetailsController.class).getDiscoveryWellDetails(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveDiscoveryWellDetails_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(DiscoveryWellDetailsForm.class, "form");
    when(discoveryWellDetailsFormService.validate(any(), any(), any(), eq(submissionDetail), any()))
        .thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryWellDetailsController.class)
                .saveDiscoveryWellDetails(submissionDetailId, null, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null))));

    verify(discoveryWellDetailsFormService, times(1)).validate(
        any(), any(), eq(validationType), eq(submissionDetail), any());
    verify(discoveryWellDetailsPersistenceService, times(1))
        .saveDiscoveryWellDetails(any(DiscoveryWellDetailsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGeneralDetailsPage_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    var bindingResult = new BeanPropertyBindingResult(DiscoveryWellDetailsForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(discoveryWellDetailsFormService.validate(any(), any(), any(), eq(submissionDetail), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryWellDetailsController.class)
                .saveDiscoveryWellDetails(submissionDetailId, null, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isOk());

    verify(discoveryWellDetailsFormService, times(1)).validate(
        any(), any(), eq(validationType), eq(submissionDetail), any());
    verify(discoveryWellDetailsPersistenceService, never()).saveDiscoveryWellDetails(any(), any());
  }

  @Test
  void getDiscoveryWellDetailsModelAndView_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var form = new DiscoveryWellDetailsForm();
    var preselectedWell = Map.of("well1", "well1");
    var preselectedTotalDepthFormation = Map.of("formation1", "formation1");

    when(discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail)).thenReturn(form);
    when(discoveryWellDetailsFormService.getPreSelectedWell(form.getWellDetailsForm().getWell())).thenReturn(preselectedWell);
    when(formationSearchService.getPreSelectedFormation(form.getWellDetailsForm().getTotalDepthFormation()))
        .thenReturn(preselectedTotalDepthFormation);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(DiscoveryWellDetailsController.class)
                        .getDiscoveryWellDetails(submissionDetailId, null)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("datumTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirDatum.class)),
            entry("discoveryStatus", DisplayableEnumOptionUtil.getDisplayableOptions(DiscoveryStatus.class)),
            entry("longitudeHemisphere", Hemisphere.getLongitudeHemisphere()),
            entry("hydrocarbonsDiscovered", DisplayableEnumOptionUtil.getDisplayableOptions(HydrocarbonsDiscovered.class)),
            entry("wellEndpoint",
                SearchSelectorService.route(on(WellRestController.class).searchWonsWells(null))),
            entry("well", preselectedWell),
            entry("formationRestEndpoint", SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))),
            entry("preSelectedTotalDepthFormation", preselectedTotalDepthFormation)
        );

    assertEquals(DiscoveryWellDetailsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)),
            TaskListController.PAGE_NAME)
    );
  }
}