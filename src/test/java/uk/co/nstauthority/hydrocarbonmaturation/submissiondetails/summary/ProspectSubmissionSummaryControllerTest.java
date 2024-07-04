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
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ProspectSubmissionSummaryController.PAGE_NAME;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionApprovalService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ProspectSubmissionSummaryController.class)
class ProspectSubmissionSummaryControllerTest extends AbstractSubmissionControllerTest {

  public static final String VIEW_NAME = "remi/submissiondetails/summary/prospectSubmissionSummary";

  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  @MockBean
  private SubmissionReadyService submissionReadyService;

  @MockBean
  private SubmissionApprovalService submissionApprovalService;

  @MockBean
  private SubmissionSummaryFormValidator submissionSummaryFormValidator;

  private UUID submissionDetailId;

  private GeneralDetailSummaryView generalDetailSummaryView;
  private SourceAndSealSummaryView sourceAndSealSummaryView;
  private HydrocarbonCaseSummaryView hydrocarbonCaseSummaryView;
  private ChanceOfSuccessSummaryView chanceOfSuccessSummaryView;
  private ReservoirPropertiesSummaryView reservoirPropertiesSummaryView;
  private ProbabilityDistributionTableView oilCaseVolumetricsTableView;
  private ProbabilityDistributionTableView gasCaseVolumetricsTableView;
  private OilCaseOutputsSummaryView oilCaseOutputsSummaryView;
  private ProbabilityDistributionTableView mixedCaseVolumetricsTableView;
  private MixedCaseOutputsSummaryView mixedCaseOutputsSummaryView;
  private TrapPropertiesSummaryView trapPropertiesSummaryView;
  private OpportunityDetailsSummaryView opportunityDetailsSummaryView;
  private OutlineForMaximumCaseSummaryView outlineForMaximumCaseSummaryView;
  private GasCaseOutputsSummaryView gasCaseOutputsSummaryView;
  private ResourceDetailSummaryView resourceDetailSummaryView;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
    generalDetailSummaryView =  GeneralDetailSummaryView.empty(SubmissionType.CREATE_PROSPECT, "prospect");
    sourceAndSealSummaryView =  SourceAndSealSummaryView.empty();
    hydrocarbonCaseSummaryView = HydrocarbonCaseSummaryView.empty();
    reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(
        SubmissionType.CREATE_PROSPECT,
        "formation",
        "sub unit",
        "age",
        "group name",
        "lithology",
        "facies",
        "connectivity",
        ProbabilityDistributionTableView.empty(),
        ProbabilityDistributionTableView.empty());
    trapPropertiesSummaryView = new TrapPropertiesSummaryView(
        "desc",
        "Other",
        "other trap type",
        "1",
        "1",
        "1",
        "10",
        "10",
        "10",
        "East",
        ReservoirDatum.ED50.getDisplayName(),
        "15/10",
        ProbabilityDistributionTableView.empty(),
        new HashMap<>()
    );
    resourceDetailSummaryView = ResourceDetailSummaryView.empty("prospect");
    chanceOfSuccessSummaryView = ChanceOfSuccessSummaryView.empty();
    oilCaseVolumetricsTableView = ProbabilityDistributionTableView.empty();
    oilCaseOutputsSummaryView = OilCaseOutputsSummaryView.empty(new SummaryBehaviour());
    gasCaseVolumetricsTableView = ProbabilityDistributionTableView.empty();
    gasCaseOutputsSummaryView = GasCaseOutputsSummaryView.empty(new SummaryBehaviour());
    mixedCaseVolumetricsTableView = ProbabilityDistributionTableView.empty();
    mixedCaseOutputsSummaryView = MixedCaseOutputsSummaryView.empty(new SummaryBehaviour());
    opportunityDetailsSummaryView = OpportunityDetailsSummaryView.empty();
    outlineForMaximumCaseSummaryView = OutlineForMaximumCaseSummaryView.empty();
  }

  @SecurityTest
  void showSubmissionSummary_whenNotProspect_assertStatusForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class)
                .showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showSubmissionSummary_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    mockCreateProspectSubmissionSummaryModelAndView();
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name(VIEW_NAME));
  }

  @SecurityTest
  void showSubmissionSummary_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);

    mockMvc.perform(
            get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showSubmissionSummary_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class)
                .showSubmissionSummary(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void processSubmission_andNotResponsibleOrganisationOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectSubmissionSummaryController.class)
                .processSubmission(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void processSubmission_assertRedirection() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionSummaryFormValidator.isValid(
        any(SubmissionSummaryForm.class),
        any(Errors.class),
        eq(submissionDetail),
        eq(ValidationType.FULL))
    ).thenReturn(true);
    mockCreateProspectSubmissionSummaryModelAndView();

    mockMvc.perform(
        post(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).processSubmission(submissionDetailId, null, null)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))));

    verify(submissionApprovalService, times(1)).submitSubmission(submissionDetail);
  }

  @Test
  void processSubmission_whenInvalidForm_assertOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(SubmissionSummaryForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(submissionSummaryFormValidator.isValid(
        any(SubmissionSummaryForm.class),
        any(Errors.class),
        eq(submissionDetail),
        eq(ValidationType.FULL))
    ).thenReturn(false);

    mockCreateProspectSubmissionSummaryModelAndView();

    mockMvc.perform(
            post(ReverseRouter.route(on(ProspectSubmissionSummaryController.class)
                .processSubmission(submissionDetailId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(submissionApprovalService, never()).submitSubmission(any());
  }

  @Test
  void getSubmissionSummaryModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(submissionReadyService.isSubmissionSubmittable(submissionDetail)).thenReturn(true);
    mockCreateProspectSubmissionSummaryModelAndView();

    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(ProspectSubmissionSummaryController.class).showSubmissionSummary(submissionDetailId)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageName", PAGE_NAME),
            entry("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))),
            entry("submittable", true),
            entry("isRegulatorOrAreaTeamUser", true),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("sourceAndSealSummaryView", sourceAndSealSummaryView),
            entry("hydrocarbonCaseSummaryView", hydrocarbonCaseSummaryView),
            entry("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView),
            entry("chanceOfSuccessSummaryView", chanceOfSuccessSummaryView),
            entry("oilCaseVolumetricsSummaryView", oilCaseVolumetricsTableView),
            entry("gasCaseVolumetricsSummaryView", gasCaseVolumetricsTableView),
            entry("oilCaseOutputsSummaryView", oilCaseOutputsSummaryView),
            entry("mixedCaseVolumetricsSummaryView", mixedCaseVolumetricsTableView),
            entry("mixedCaseOutputsSummaryView", mixedCaseOutputsSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView),
            entry("resourceDetailSummaryView", resourceDetailSummaryView),
            entry("opportunityDetailsSummaryView", opportunityDetailsSummaryView),
            entry("accordionId", submissionDetailId)
    );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME));
  }

  private void mockCreateProspectSubmissionSummaryModelAndView() {
    var modelAndView = new ModelAndView(VIEW_NAME)
        .addObject("generalDetailsSummaryView", generalDetailSummaryView)
        .addObject("sourceAndSealSummaryView", sourceAndSealSummaryView)
        .addObject("chanceOfSuccessSummaryView", chanceOfSuccessSummaryView)
        .addObject("hydrocarbonCaseSummaryView", hydrocarbonCaseSummaryView)
        .addObject("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView)
        .addObject("trapPropertiesSummaryView", trapPropertiesSummaryView)
        .addObject("oilCaseVolumetricsSummaryView", oilCaseVolumetricsTableView)
        .addObject("oilCaseOutputsSummaryView", oilCaseOutputsSummaryView)
        .addObject("gasCaseVolumetricsSummaryView", gasCaseVolumetricsTableView)
        .addObject("gasCaseOutputsSummaryView", gasCaseOutputsSummaryView)
        .addObject("mixedCaseVolumetricsSummaryView", mixedCaseVolumetricsTableView)
        .addObject("mixedCaseOutputsSummaryView", mixedCaseOutputsSummaryView)
        .addObject("opportunityDetailsSummaryView", opportunityDetailsSummaryView)
        .addObject("outlineForMaximumCaseSummaryView", outlineForMaximumCaseSummaryView)
        .addObject("resourceDetailSummaryView", resourceDetailSummaryView)
        .addObject("submittable", true)
        .addObject("gasCase", true)
        .addObject("oilCase", true)
        .addObject("isSubmissionUpdate", false)
        .addObject("mixedCase", true)
        .addObject("accordionId", submissionDetail.getId())
        .addObject("isOffshore", true);
    when(submissionSummaryModelService.createProspectSubmissionSummaryModelAndView(
        submissionDetail,
        VIEW_NAME
    )).thenReturn(modelAndView);
    when(internalTeamService.isRegulatorOrAreaTeamUser()).thenReturn(true);
  }
}