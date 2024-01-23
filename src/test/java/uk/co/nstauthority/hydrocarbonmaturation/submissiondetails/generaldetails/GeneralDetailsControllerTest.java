package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = GeneralDetailsController.class)
class GeneralDetailsControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private GeneralDetailsFormService generalDetailsFormService;

  @MockBean
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @MockBean
  private GeneralDetailsFormValidator generalDetailsFormValidator;

  @MockBean
  private OnshoreOffshoreService onshoreOffshoreService;

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
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
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
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.OFFSHORE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(generalDetailsFormValidator.isValid(any(), any(), any(), any())).thenReturn(true);

    mockMvc.perform(
        post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(generalDetailsPersistenceService, times(1))
        .saveGeneralDetails(any(GeneralDetailsForm.class), eq(submissionDetail), eq(OnshoreOffshore.OFFSHORE));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGeneralDetailsPage_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.OFFSHORE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(generalDetailsFormValidator.isValid(any(), any(), any(), any())).thenReturn(false);

    mockMvc.perform(
        post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(submissionDetailId, null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verifyNoInteractions(generalDetailsPersistenceService);
  }

  @Test
  void getGeneralDetailsModelAndView_offshoreProspectSubmission_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.OFFSHORE);
    var form = new GeneralDetailsForm();
    var preselectedLicenceApplication = Map.of("licenceapp1", "licence app 1");

    when(generalDetailsFormService.getGeneralDetailsForm(submissionDetail)).thenReturn(form);
    when(generalDetailsFormService.getPreSelectedLicenceApplication(form.getLicenceApplication())).thenReturn(preselectedLicenceApplication);

    var licenceView = new LicenceView("1/1", 1, LicenceStatus.EXTANT);
    var licenceViewList = List.of(licenceView);
    when(generalDetailsFormService.getLicenceViews(form)).thenReturn(licenceViewList);
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
            entry("informationSources", InformationSource.getInformationSource()),
            entry("licenceApplication", preselectedLicenceApplication),
            entry("licenceApplicationEndpoint",
                SearchSelectorService.route(on(LicenceApplicationRestController.class).searchLicenceApplicationsWithManualEntry(null))),
            entry("quadrantBlocksApiEndpoint",
                SearchSelectorService.route(on(ReferenceBlockRestController.class).searchOffshoreQuadrantBlocks(null))),
            entry("resourceType", ResourceType.PROSPECT.name().toLowerCase()),
            entry("prospectSubmission", true),
            entry("discoverySubmission", false),
            entry("fieldSubmission", false),
            entry("relinquishmentSubmission", false),
            entry("alreadyAddedQuadrantBlocks", quadrantBlockViewList),
            entry("licenceEndpoint",
                SearchSelectorService.route(on(LicenceRestController.class).searchExtantLicences(null))),
            entry("alreadyAddedLicences", licenceViewList),
            entry("isOffshore", true),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isRegulatorSubmission", true)
        );
    assertEquals(GeneralDetailsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );
  }

  @Test
  void getGeneralDetailsModelAndView_onshoreDiscoverySubmission_assertModelProperties() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.ONSHORE);
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var submission = new Submission();
    submission.setResource(discovery);
    submissionDetail.setSubmission(submission);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var form = new GeneralDetailsForm();
    var preselectedLicenceApplication = Map.of("licenceapp1", "licence app 1");

    when(generalDetailsFormService.getGeneralDetailsForm(submissionDetail)).thenReturn(form);
    when(generalDetailsFormService.getPreSelectedLicenceApplication(form.getLicenceApplication())).thenReturn(
        preselectedLicenceApplication);
    var licenceView = new LicenceView("1/1", 1, LicenceStatus.EXTANT);
    var licenceViewList = List.of(licenceView);
    when(generalDetailsFormService.getLicenceViews(form)).thenReturn(licenceViewList);
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
                SearchSelectorService.route(on(ReferenceBlockRestController.class).searchOnshoreQuadrantBlocks(null))),
            entry("resourceType", ResourceType.DISCOVERY.name().toLowerCase()),
            entry("prospectSubmission", false),
            entry("discoverySubmission", true),
            entry("fieldSubmission", false),
            entry("relinquishmentSubmission", false),
            entry("alreadyAddedQuadrantBlocks", quadrantBlockViewList),
            entry("licenceEndpoint",
                SearchSelectorService.route(on(LicenceRestController.class).searchExtantLicences(null))),
            entry("alreadyAddedLicences", licenceViewList),
            entry("isOffshore", false),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("isRegulatorSubmission", true)
        );
    assertEquals(GeneralDetailsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)),
            TaskListController.PAGE_NAME)
    );
  }

  @Test
  void getGeneralDetailsModelAndView_whenCorrection_assertLicenceEndpoint() throws Exception {
    submissionDetail.setCorrection(true);
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
            entry("licenceEndpoint",
                SearchSelectorService.route(on(LicenceRestController.class).searchLiveLicences(null)))
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getGeneralDetailsModelAndView_fieldSubmission_assertModelProperties(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.ONSHORE);
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var submission = new Submission();
    submission.setResource(discovery);
    submissionDetail.setSubmission(submission);
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
            entry("fieldSubmission", true),
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("driveMechanismOptions", DriveMechanism.getDriveMechanismOptionsWithHintText())
        );
    assertEquals(GeneralDetailsForm.class, modelMap.get("form").getClass());
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"},
      mode = EnumSource.Mode.INCLUDE)
  void getGeneralDetailsModelAndView_relinquishmentSubmission_assertRelinquishmentTrue(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail)).thenReturn(resourceDetail);
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
            entry("fieldSubmission", false),
            entry("relinquishmentSubmission", true)
        )
        .doesNotContainKey("informationSources");
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .filter(submissionType -> submissionType != SubmissionType.CREATE_FIELD)
        .map(Arguments::of);
  }
}