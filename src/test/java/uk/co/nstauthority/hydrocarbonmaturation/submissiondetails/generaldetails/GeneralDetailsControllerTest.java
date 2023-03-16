package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

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

import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = GeneralDetailsController.class)
class GeneralDetailsControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private GeneralDetailsFormService generalDetailsFormService;

  @MockBean
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetailId = submissionDetail.getId();
  }

  @SecurityTest
  void getGeneralDetails_whenResponsibleOrganisation_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(generalDetailsFormService.getGeneralDetailsForm(submissionDetail)).thenReturn(new GeneralDetailsForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    mockMvc.perform(
        get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/generalDetails"));
  }

  @SecurityTest
  void getGeneralDetails_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(
            get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getGeneralDetails_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveGeneralDetails_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(GeneralDetailsController.class)
                .saveGeneralDetails(submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGeneralDetailsPage_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(GeneralDetailsForm.class, "form");
    when(generalDetailsFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(generalDetailsFormService, times(1)).validate(any(), any(), eq(validationType), any());
    verify(generalDetailsPersistenceService, times(1)).saveGeneralDetails(any(GeneralDetailsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGeneralDetailsPage_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    var bindingResult = new BeanPropertyBindingResult(GeneralDetailsForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(generalDetailsFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(generalDetailsFormService, times(1)).validate(any(), any(), eq(validationType), any());
    verify(generalDetailsPersistenceService, never()).saveGeneralDetails(any(), any());
  }

  @Test
  void getGeneralDetailsModelAndView_prospectSubmission_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var form = new GeneralDetailsForm();
    var preselectedLicenceApplication = Map.of("licenceapp1", "licence app 1");
    var preselectedOrganisation = Map.of("25", "Shell Gas");

    when(generalDetailsFormService.getGeneralDetailsForm(submissionDetail)).thenReturn(form);
    when(generalDetailsFormService.getPreSelectedLicenceApplication(form.getLicenceApplication())).thenReturn(preselectedLicenceApplication);

    var quadrantBlockView1 = new QuadrantAndBlockView("test", "test", true);
    var quadrantBlockViewList = List.of(quadrantBlockView1);
    when(generalDetailsFormService.getQuadrantAndBlockViews(form)).thenReturn(quadrantBlockViewList);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore()),
            entry("informationSources", InformationSource.getInformationSource()),
            entry("licenceApplication", preselectedLicenceApplication),
            entry("licenceApplicationEndpoint",
                SearchSelectorService.route(on(LicenceApplicationRestController.class).searchLicenceApplicationsWithManualEntry(null))),
            entry("quadrantBlocksApiEndpoint",
                SearchSelectorService.route(on(ReferenceBlockRestController.class).searchQuadrantBlocks(null))),
            entry("resourceType", ResourceType.PROSPECT.name().toLowerCase()),
            entry("prospectSubmission", true),
            entry("discoverySubmission", false),
            entry("relinquishmentSubmission", false),
            entry("alreadyAddedQuadrantBlocks", quadrantBlockViewList)
        );
    assertEquals(GeneralDetailsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );
  }

  @Test
  void getGeneralDetailsModelAndView_discoverySubmission_assertModelProperties() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    submissionDetail.setResourceId(discovery);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var form = new GeneralDetailsForm();
    var preselectedLicenceApplication = Map.of("licenceapp1", "licence app 1");

    when(generalDetailsFormService.getGeneralDetailsForm(submissionDetail)).thenReturn(form);
    when(generalDetailsFormService.getPreSelectedLicenceApplication(form.getLicenceApplication())).thenReturn(
        preselectedLicenceApplication);

    var quadrantBlockView1 = new QuadrantAndBlockView("test", "test", true);
    var quadrantBlockViewList = List.of(quadrantBlockView1);
    when(generalDetailsFormService.getQuadrantAndBlockViews(form)).thenReturn(quadrantBlockViewList);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("informationSources", InformationSource.getDiscoveryInformationSource()),
            entry("licenceApplication", preselectedLicenceApplication),
            entry("licenceApplicationEndpoint",
                SearchSelectorService.route(
                    on(LicenceApplicationRestController.class).searchLicenceApplicationsWithManualEntry(null))),
            entry("quadrantBlocksApiEndpoint",
                SearchSelectorService.route(on(ReferenceBlockRestController.class).searchQuadrantBlocks(null))),
            entry("resourceType", ResourceType.DISCOVERY.name().toLowerCase()),
            entry("prospectSubmission", false),
            entry("discoverySubmission", true),
            entry("relinquishmentSubmission", false),
            entry("alreadyAddedQuadrantBlocks", quadrantBlockViewList)
        );
    assertEquals(GeneralDetailsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)),
            TaskListController.PAGE_NAME)
    );
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"},
      mode = EnumSource.Mode.INCLUDE)
  void getGeneralDetailsModelAndView_relinquishmentSubmission_assertRelinquishmentTrue(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var form = new GeneralDetailsForm();
    when(generalDetailsFormService.getGeneralDetailsForm(submissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetailId, null)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("prospectSubmission", submissionDetail.isProspectSubmission()),
            entry("discoverySubmission", submissionDetail.isDiscoverySubmission()),
            entry("relinquishmentSubmission", true)
        )
        .doesNotContainKey("informationSources");
  }
}