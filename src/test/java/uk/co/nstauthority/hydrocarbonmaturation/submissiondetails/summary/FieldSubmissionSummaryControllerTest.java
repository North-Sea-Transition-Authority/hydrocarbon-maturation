package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

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
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.DiscoverySubmissionSummaryController.PAGE_NAME;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation.FieldRegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionApprovalService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshoreService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = FieldSubmissionSummaryController.class)
class FieldSubmissionSummaryControllerTest extends AbstractSubmissionControllerTest {

  public static final String VIEW_NAME = "remi/submissiondetails/summary/fieldSubmissionSummary";

  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  @MockBean
  private SubmissionReadyService submissionReadyService;

  @MockBean
  private SubmissionApprovalService submissionApprovalService;

  @MockBean
  private SubmissionSummaryFormValidator submissionSummaryFormValidator;

  @MockBean
  private OnshoreOffshoreService onshoreOffshoreService;

  @MockBean
  private FieldRegulatorManagedInformationSummaryService fieldRegulatorManagedInformationSummaryService;

  @MockBean
  private FieldMapViewService fieldMapViewService;

  private List<SummarySection> summarySections;
  private List<ReservoirSummary> reservoirSummaries;

  private SubmissionDetail submissionDetail;
  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    summarySections = Collections.emptyList();
    reservoirSummaries = Collections.emptyList();
    when(submissionDetailService.getSubmissionDetail(submissionDetailId)).thenReturn(Optional.of(submissionDetail));
    var field = new Field();
    field.setPrePearsOrganisationId(1);
    when(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource()))
        .thenReturn(field);
  }

  @SecurityTest
  void showSubmissionSummary_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var managedInformationCard = SummaryCard.simpleSummaryCard(new SummaryDataView(List.of()));
    when(fieldRegulatorManagedInformationSummaryService
        .getFieldRegulatorManagedInformationSummaryCard(submissionDetail.getSubmission().getResource()))
        .thenReturn(managedInformationCard);
    mockCreateFieldSubmissionSummaryModelAndView();
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);

    var field = FieldTestUtils.createField("Alex", OnshoreOffshore.ONSHORE);
    when(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource()))
        .thenReturn(field);

    mockMvc.perform(
            get(ReverseRouter.route(
                on(FieldSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME));
  }

  @SecurityTest
  void showSubmissionSummary_whenNotDiscovery_assertStatusForbidden() throws Exception {
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(
                on(FieldSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showSubmissionSummary_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(
                on(FieldSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().is4xxClientError());
  }

  @SecurityTest
  void showSubmissionSummary_andUserNotInResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(
                on(FieldSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().is4xxClientError());
  }

  @SecurityTest
  void processSubmission_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionSummaryFormValidator.isValid(
        any(Errors.class),
        eq(submissionDetail),
        eq(USER))
    ).thenReturn(true);
    mockCreateFieldSubmissionSummaryModelAndView();
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);
    mockMvc.perform(
            post(ReverseRouter.route(
                on(FieldSubmissionSummaryController.class).processSubmission(submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(
            on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))));

    verify(submissionApprovalService, times(1)).submitSubmission(submissionDetail, USER);
  }

  @Test
  void processSubmission_whenInvalidForm_assertOk() throws Exception {
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var managedInformationCard = SummaryCard.simpleSummaryCard(new SummaryDataView(List.of()));
    when(fieldRegulatorManagedInformationSummaryService
        .getFieldRegulatorManagedInformationSummaryCard(submissionDetail.getSubmission().getResource()))
        .thenReturn(managedInformationCard);
    var bindingResult = new BeanPropertyBindingResult(SubmissionSummaryForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(submissionSummaryFormValidator.isValid(
        any(Errors.class),
        eq(submissionDetail),
        eq(USER))
    ).thenReturn(false);

    var field = FieldTestUtils.createField("Alex", OnshoreOffshore.ONSHORE);
    when(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource()))
        .thenReturn(field);

    mockCreateFieldSubmissionSummaryModelAndView();

    mockMvc.perform(
            post(ReverseRouter.route(
                on(FieldSubmissionSummaryController.class).processSubmission(submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(submissionApprovalService, never()).submitSubmission(any(), any());
  }

  @SecurityTest
  void processSubmission_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);

    mockMvc.perform(
            post(ReverseRouter.route(
                on(FieldSubmissionSummaryController.class).processSubmission(submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionReadyService.isSubmissionSubmittable(submissionDetail, USER)).thenReturn(true);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.OFFSHORE);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    var managedInformationCard = SummaryCard.simpleSummaryCard(new SummaryDataView(List.of()));
    when(fieldRegulatorManagedInformationSummaryService
        .getFieldRegulatorManagedInformationSummaryCard(submissionDetail.getSubmission().getResource()))
        .thenReturn(managedInformationCard);
    when(fieldRegulatorManagedInformationSummaryService.getAssociatedDiscoveriesMap(submissionDetail.getSubmission().getResource()))
        .thenReturn(Map.of("Name", "url"));

    var field = FieldTestUtils.createField("Alex", OnshoreOffshore.ONSHORE);
    when(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource()))
        .thenReturn(field);
    var fieldMapView = new FieldMapView("", "");
    when(fieldMapViewService.getFieldGeoJsonUrl(field))
        .thenReturn(Optional.of(fieldMapView));

    mockCreateFieldSubmissionSummaryModelAndView();

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(
                on(FieldSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageName", PAGE_NAME),
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null))),
            entry("submittable", true),
            entry("summarySections", summarySections),
            entry("reservoirSummaries", reservoirSummaries),
            entry("accordionId", submissionDetailId),
            entry("isOffshore", true),
            entry("fieldRegulatorManagedInformationSummaryCard", managedInformationCard),
            entry("fieldMapView", fieldMapView),
            entry("associatedDiscoveriesMap", Map.of("Name", "url"))
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)), TaskListController.PAGE_NAME));
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties_noFieldMapView() throws Exception {
    setUpWhenUserIsResponsibleTeam_forField_passSecurity(USER, submissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionReadyService.isSubmissionSubmittable(submissionDetail, USER)).thenReturn(true);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.OFFSHORE);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    var managedInformationCard = SummaryCard.simpleSummaryCard(new SummaryDataView(List.of()));
    when(fieldRegulatorManagedInformationSummaryService
        .getFieldRegulatorManagedInformationSummaryCard(submissionDetail.getSubmission().getResource()))
        .thenReturn(managedInformationCard);

    var field = FieldTestUtils.createField("Alex", OnshoreOffshore.ONSHORE);
    when(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource()))
        .thenReturn(field);
    when(fieldMapViewService.getFieldGeoJsonUrl(field))
        .thenReturn(Optional.empty());

    mockCreateFieldSubmissionSummaryModelAndView();

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(
                on(FieldSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap).doesNotContainKey("fieldMapView");
  }

  private void mockCreateFieldSubmissionSummaryModelAndView() {
    var modelAndView = new ModelAndView(VIEW_NAME)
        .addObject("summarySections", summarySections)
        .addObject("reservoirSummaries", reservoirSummaries)
        .addObject("submittable", true)
        .addObject("isSubmissionUpdate", false)
        .addObject("accordionId", submissionDetail.getId());
    when(submissionSummaryModelService.createFieldSubmissionSummaryModelAndView(submissionDetail, VIEW_NAME, USER)
    ).thenReturn(modelAndView);

  }

}
