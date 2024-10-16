package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ResourceDetailsController.class)
class ResourceDetailsControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private ResourceDetailsFormService resourceDetailsFormService;

  @MockBean
  private ResourceDetailsFormValidator resourceDetailsFormValidator;

  @SecurityTest
  void getResourceDetails_whenResponsibleOrganisation_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.empty());
    when(resourceDetailsFormService.resourceDetailEntityToForm(any(ResourceDetail.class))).thenReturn(new ResourceDetailsForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceDetailsController.class)
                .getResourceDetails(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/resourceDetails"));
  }

  @SecurityTest
  void getGeneralDetails_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceDetailsController.class).getResourceDetails(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getResourceDetails_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceDetailsController.class)
                .getResourceDetails(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveResourceDetails_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(ResourceDetailsController.class)
                .saveResourceDetails(SUBMISSION_DETAIL_ID, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(ValidationType.class)
  void saveResourceDetails_whenValidForm_assertSaveAndRedirect(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    when(resourceDetailsFormValidator.isValid(any(), any(), any(), any())).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ResourceDetailsController.class)
                .saveResourceDetails(SUBMISSION_DETAIL_ID, null, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(SUBMISSION_DETAIL_ID, null))));
    verify(resourceDetailsPersistenceService, times(1))
        .saveResourceDetails(any(ResourceDetailsForm.class), eq(submissionDetail), eq(resourceDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveResourceDetailsPage_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    var bindingResult = new BeanPropertyBindingResult(ResourceDetailsForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(resourceDetailsFormValidator.isValid(any(), any(), any(), any())).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    mockMvc.perform(
            post(ReverseRouter.route(on(ResourceDetailsController.class)
                .saveResourceDetails(SUBMISSION_DETAIL_ID, null, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(resourceDetailsPersistenceService, never()).saveResourceDetails(any(), any(), any());
  }

  @Test
  void getResourceDetailsModelAndView_assertModelProperties() throws Exception {
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    when(resourceDetailsFormService.resourceDetailEntityToForm(any(ResourceDetail.class))).thenReturn(new ResourceDetailsForm());
    var preselectedOrganisation = Map.of("25", "Shell Gas");
    when(resourceDetailsFormService.getPreSelectedOrganisationUnit(resourceDetail, submissionDetail))
        .thenReturn(preselectedOrganisation);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(ResourceDetailsController.class)
                .getResourceDetails(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore()),
            entry("organisationUnitEndpoint", SearchSelectorService.route(on(OrganisationUnitRestController.class)
                    .searchOrganisationUnitsWithSubmissionDetail(null, submissionDetail.getId(), null))),
            entry("responsibleOrganisation", preselectedOrganisation),
            entry("resourceType", submissionDetail.getSubmission().getResource().getType().name().toLowerCase()),
            entry("isRegulatorOrAreaTeamUser", false),
            entry("relinquishmentSubmission", false)
        );
    assertEquals(ResourceDetailsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)),
            WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(SUBMISSION_DETAIL_ID, null)),
            TaskListController.PAGE_NAME)
    );
  }
}